package edu.wisc.ischool.wiscir.common;

public class PositionBO implements Comparable<PositionBO> {

    private String term;

    private Integer docID;

    private Long tf;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Integer getDocID() {
        return docID;
    }

    public void setDocID(Integer docID) {
        this.docID = docID;
    }

    public Long getTf() {
        return tf;
    }

    public void setTf(Long tf) {
        this.tf = tf;
    }


    @Override
    public int compareTo(PositionBO o) {
        if (this.getTerm().equals(o.getTerm())) {
            return this.docID.compareTo(o.docID);
        } else {
            return this.getTerm().compareTo(o.getTerm());
        }
    }

    @Override
    public String toString() {
        return "PositionBO{" +
                "term='" + term + '\'' +
                ", docID=" + docID +
                ", tf=" + tf +
                '}';
    }
}
