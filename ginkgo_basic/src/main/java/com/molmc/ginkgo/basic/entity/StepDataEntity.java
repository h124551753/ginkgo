package com.molmc.ginkgo.basic.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by dylan on 2016/1/30.
 */
@Entity
public class StepDataEntity {

    public StepDataEntity(long id, String today, String step) {
        this.id = id;
        this.today = today;
        this.step = step;
    }

    public StepDataEntity(){

    }

    // 指定自增，每个对象需要有一个主键
    @Id
    private long id;

    private String today;
    private String step;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "StepDataEntity{" +
                "id=" + id +
                ", today='" + today + '\'' +
                ", step='" + step + '\'' +
                '}';
    }
}
