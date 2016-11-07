package com.zqh.crunch;

/**
 * Created by hadoop on 15-1-14.
 */
public class Trade {

    private String stock_symbol;
    private Long trade_time;
    private Double trade_price;
    private Integer sequence_num;

    public String getStock_symbol() {
        return stock_symbol;
    }

    public void setStock_symbol(String stock_symbol) {
        this.stock_symbol = stock_symbol;
    }

    public Long getTrade_time() {
        return trade_time;
    }

    public void setTrade_time(Long trade_time) {
        this.trade_time = trade_time;
    }

    public Double getTrade_price() {
        return trade_price;
    }

    public void setTrade_price(Double trade_price) {
        this.trade_price = trade_price;
    }

    public Integer getSequence_num() {
        return sequence_num;
    }

    public void setSequence_num(Integer sequence_num) {
        this.sequence_num = sequence_num;
    }
}
