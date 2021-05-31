package com.example.idiary;

public enum DayOfWeekRus {

    MONDAY(1, "ПН"),
    TUESDAY(2, "ВТ"),
    WEDNESDAY(3, "СР"),
    THURSDAY(4, "ЧТ"),
    FRIDAY(5, "ПТ"),
    SATURDAY(6, "СБ"),
    SUNDAY(7, "ВС");

    int number;
    String shortRus;

    DayOfWeekRus(int number, String shortRus) {
        this.number = number;
        this.shortRus = shortRus;
    }
}
