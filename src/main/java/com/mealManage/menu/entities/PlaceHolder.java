package com.mealManage.menu.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "pm_placeholders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String placeholder;
    private boolean isPublished;
    private String content;
}
