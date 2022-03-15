package edu.wisc.ischool.wiscir.common;

import java.util.List;

public class TermMergeBO {

    private String term;

    private Long df;

    private List<PositionBO> positionBOList;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Long getDf() {
        return df;
    }

    public void setDf(Long df) {
        this.df = df;
    }

    public List<PositionBO> getPositionBOList() {
        return positionBOList;
    }

    public void setPositionBOList(List<PositionBO> positionBOList) {
        this.positionBOList = positionBOList;
    }
}
