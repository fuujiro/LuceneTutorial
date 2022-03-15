package edu.wisc.ischool.wiscir.common;

public class DictBO {

    private String term;

    private Long df;

    private Long offset;

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

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "DictBO{" +
                "term='" + term + '\'' +
                ", df=" + df +
                ", offset=" + offset +
                '}';
    }
}
