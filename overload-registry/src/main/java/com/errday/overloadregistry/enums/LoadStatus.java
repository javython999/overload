package com.errday.overloadregistry.enums;

public enum LoadStatus {
    REGISTERED("등록", "bg-blue-100 text-blue-700"),
    RUNNING("진행", "bg-purple-100 text-purple-700"),
    COMPLETED("완료", "bg-green-100 text-green-700"),
    FAILED("실패", "bg-red-100 text-red-700");

    private final String text;
    private final String className;

    LoadStatus(String text, String className) {
        this.text = text;
        this.className = className;
    }

    public String getText() {
        return text;
    }

    public String getClassName() {
        return className;
    }
}
