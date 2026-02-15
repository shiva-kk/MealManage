package com.mealManage;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.mealManage.service.FileProcessor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.*;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptAllFileListFilter;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
public class SftpConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${sftp.host}")
    private String sftpHost;

    @Value("${sftp.port:22}")
    private int sftpPort;

    @Value("${sftp.user}")
    private String sftpUser;

    @Value("${sftp.password}")
    private String sftpPassword;

    @Value("${sftp.remote.directory.download}")
    private String sftpRemoteDirectoryDownload;

    @Value("${sftp.local.directory.download:${java.io.tmpdir}/localDownload}")
    private String sftpLocalDirectoryDownload;

    @Value("${sftp.remote.directory.download.filter:*.*}")
    private String sftpRemoteDirectoryDownloadFilter;

    @Value("${sftp.remote.directory.success}")
    private String successDir;

    @Value("${sftp.remote.directory.failure}")
    private String failureDir;

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");


    @Autowired
    FileProcessor fileProcessor;

    @Autowired
    ConfigurableApplicationContext context;

    @Bean
    @Order(1)
    public SessionFactory<LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost(sftpHost);
        factory.setPort(sftpPort);
        factory.setUser(sftpUser);
        factory.setPassword(sftpPassword);
        factory.setAllowUnknownKeys(true);
        return new CachingSessionFactory<>(factory);
    }

    @Bean
    @Order(2)
    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
        SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
        fileSynchronizer.setDeleteRemoteFiles(true);
        fileSynchronizer.setRemoteDirectory(sftpRemoteDirectoryDownload);
        fileSynchronizer
                .setFilter(new SftpSimplePatternFileListFilter(sftpRemoteDirectoryDownloadFilter));
        return fileSynchronizer;
    }

    @Bean
    @Order(3)
   // @InboundChannelAdapter(channel = "fromSftpChannel", poller = @Poller(cron = "0/5 * * * * *"))
    @InboundChannelAdapter(channel = "fromSftpChannel", poller = @Poller(fixedDelay = "5000"))
    public MessageSource<File> sftpMessageSource() {
        SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(
                sftpInboundFileSynchronizer());
        source.setLocalDirectory(new File(sftpLocalDirectoryDownload));
        source.setAutoCreateLocalDirectory(true);
        //source.setLocalFilter(new AcceptOnceFileListFilter<>());
        source.setLocalFilter(new AcceptAllFileListFilter<>());
        return source;
    }

    @Bean
    @Order(4)
    @ServiceActivator(inputChannel = "fromSftpChannel")
    public MessageHandler resultFileHandler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                logger.info("received a file, local file path:: {}", message.getPayload());
                SFTPGateway gateway = context.getBean(SFTPGateway.class);
                File file = (File) message.getPayload();

                boolean processStatus = false;
                try {
                    processStatus = fileProcessor.process(file);

                } catch (Exception e) {
                    logger.error("Error while processing the file: {}", ExceptionUtils.getStackTrace(e));
                } finally {
                    if (processStatus) {
                        /* move the file to success folder */
                        gateway.sendToSftpOnSuccess(file);
                    } else {
                        /* move the file to filed folder */
                        gateway.sendToSftpOnFailure(file);
                    }

                    /* Delete the local file */
                    if (file.exists()) {
                        String localPath = sftpLocalDirectoryDownload + "/" + file.getName();
                        logger.info("local file directory path: {}", localPath);
                        Path path = Paths.get(localPath);
                        for(int i=0;i<3; i++) {
                            try {
                                Files.delete(path);
                                logger.info("local file Deleted successfully");
                                if(!file.exists()){
                                    break;
                                }
                            } catch (IOException e) {
                                logger.error("Error while deleting local file: {}", ExceptionUtils.getStackTrace(e));
                                try {
                                    TimeUnit.SECONDS.sleep(5L);
                                } catch (InterruptedException interruptedException) {
                                    logger.error("interruptedException: {}", ExceptionUtils.getStackTrace(interruptedException));
                                }
                            }
                        }

                    }
                }

            }
        };
    }

    @Bean
    @ServiceActivator(inputChannel = "sftpChannelSuccessDest")
    public MessageHandler handlerSuccess() {
        SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory());
        handler.setRemoteDirectoryExpression(new LiteralExpression(successDir));
        handler.setFileNameGenerator(message -> {
            if (message.getPayload() instanceof File) {
                return replaceLast(((File) message.getPayload()).getName(), ".",
                        "_" + simpleDateFormat.format(new Date()) + ".");
            } else {
                throw new IllegalArgumentException("File expected as payload.");
            }
        });
        return handler;
    }

    @Bean
    @ServiceActivator(inputChannel = "sftpChannelFailureDest")
    public MessageHandler handlerFailure() {
        SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory());
        handler.setRemoteDirectoryExpression(new LiteralExpression(failureDir));
        handler.setFileNameGenerator(message -> {
            if (message.getPayload() instanceof File) {
                return replaceLast(((File) message.getPayload()).getName(), ".",
                        "_" + simpleDateFormat.format(new Date()) + ".");
            } else {
                throw new IllegalArgumentException("File expected as payload.");
            }
        });
        return handler;
    }

    @MessagingGateway
    public interface SFTPGateway {
        @Gateway(requestChannel = "sftpChannelSuccessDest")
        void sendToSftpOnSuccess(File file);

        @Gateway(requestChannel = "sftpChannelFailureDest")
        void sendToSftpOnFailure(File file);

    }

    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length());
        } else {
            return string;
        }
    }
}
