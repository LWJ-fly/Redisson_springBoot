package test.redisson.enums;

/**
 * 类描述：删除状态
 * @author 8529
 * @date 2021年8月20日 下午3:52:31
 * 版权所有 Copyright www.danhantc.com
 */
public enum DeleteStatus {
    
    /**
     * 正常
     */
    NORMAL(0, "正常"),
    
    /**
     * 已删除
     */
    DELETED(1, "已删除");
    
    private Integer code;
    private String desc;
    
    DeleteStatus(Integer code, String msg) {
        this.code = code;
        this.desc = msg;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
