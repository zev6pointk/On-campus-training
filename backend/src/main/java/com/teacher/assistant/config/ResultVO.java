package com.teacher.assistant.config;

import lombok.Data;

/**
 * 统一返回结果
 *
 * @author teacher
 * @since 2025-10-28
 */
@Data
public class ResultVO<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 成功标记
     */
    private Boolean success;

    public ResultVO() {}

    public ResultVO(Integer code, String message, T data, Boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    /**
     * 成功返回
     */
    public static <T> ResultVO<T> success() {
        return new ResultVO<>(200, "操作成功", null, true);
    }

    /**
     * 成功返回（带数据）
     */
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(200, "操作成功", data, true);
    }

    /**
     * 成功返回（自定义消息）
     */
    public static <T> ResultVO<T> success(String message, T data) {
        return new ResultVO<>(200, message, data, true);
    }

    /**
     * 失败返回
     */
    public static <T> ResultVO<T> error() {
        return new ResultVO<>(500, "操作失败", null, false);
    }

    /**
     * 失败返回（自定义消息）
     */
    public static <T> ResultVO<T> error(String message) {
        return new ResultVO<>(500, message, null, false);
    }

    /**
     * 失败返回（自定义状态码和消息）
     */
    public static <T> ResultVO<T> error(Integer code, String message) {
        return new ResultVO<>(code, message, null, false);
    }
}
