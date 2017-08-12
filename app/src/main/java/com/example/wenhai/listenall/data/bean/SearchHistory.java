package com.example.wenhai.listenall.data.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public final class SearchHistory {
    @Id
    private Long id;
    private String keyword;
    private long searchTime;
    @Generated(hash = 20276390)
    public SearchHistory(Long id, String keyword, long searchTime) {
        this.id = id;
        this.keyword = keyword;
        this.searchTime = searchTime;
    }
    @Generated(hash = 1905904755)
    public SearchHistory() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getKeyword() {
        return this.keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public long getSearchTime() {
        return this.searchTime;
    }
    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }
}
