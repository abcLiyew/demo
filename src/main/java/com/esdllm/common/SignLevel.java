package com.esdllm.common;


public enum SignLevel {
    ZERO(15.0,"路人","排斥"),
    ONE(40.0,"陌生","冷漠"),
    TWO(70.0,"相遇","警惕"),
    THREE(120.0,"初识","可以交流"),
    FOUR(180.0,"熟悉","友善"),
    FIVE(250.0,"熟知","信任"),
    SIX(330.0,"深交","喜爱"),
    SEVEN(420.0,"情深","热情"),
    EIGHT(520.0,"情投意合","亲密"),
    NINE(630.0,"挚友","无话不谈");


    private Double empirical;


    public void setAttitude(String attitude) {
        this.attitude = attitude;
    }

    private String opinion;

    private String attitude;

    private SignLevel(Double empirical, String opinion, String attitude) {
        this.empirical = empirical;
        this.opinion = opinion;
        this.attitude = attitude;
    }
    public Double getEmpirical() {
        return empirical;
    }

    public void setEmpirical(Double empirical) {
        this.empirical = empirical;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getAttitude() {
        return attitude;
    }
    @Override
    public String toString() {
        return "好感度等级："+this.opinion+"\n对你的态度："+this.attitude;
    }
}
