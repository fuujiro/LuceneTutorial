package edu.wisc.ischool.wiscir.examples;

import edu.wisc.ischool.wiscir.common.DictBO;
import edu.wisc.ischool.wiscir.common.PositionBO;
import edu.wisc.ischool.wiscir.common.TermMergeBO;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static String targetDirectoryPath = "output";

    private static List<TermMergeBO> termMergeBOList = new ArrayList<>();

    private static List<DictBO> dictBOList = new ArrayList<>();

    private static List<PositionBO> positionBOList = new ArrayList<>();

    private static List<PositionBO> newPositionBOList = new ArrayList<>();

    public static void main(String[] args) {

        for (int i = 0; i < 200; i++) {
            getDocVec(i);
        }
        positionBOList.sort(PositionBO::compareTo);

//        System.out.println(positionBOList);

        for (PositionBO positionBO : positionBOList) {
            boolean flag = true;//给整合后集合添加子元素标志，true：添加，false:不添加，其年龄相加
            for (PositionBO bo : newPositionBOList) {
                if (bo.getTerm().equals(positionBO.getTerm())) {//判断姓名是否相同
                    bo.setTf(bo.getTf() + positionBO.getTf());//姓名相同时，年龄相加
                    flag = false;
                }
            }
            if (flag) {
                PositionBO bo = new PositionBO();
                bo.setTerm(positionBO.getTerm());
                bo.setTf(positionBO.getTf());
                newPositionBOList.add(bo);//给整合后集合添加子元素
            }
        }

//        System.out.println(newPositionBOList);

        Long offset = 0l;
        for (PositionBO positionBO : newPositionBOList) {
            DictBO dictBO = new DictBO();
            dictBO.setTerm(positionBO.getTerm());
            dictBO.setDf(positionBO.getTf());
            dictBO.setOffset(offset);
            offset += positionBO.getTf();
            dictBOList.add(dictBO);//给整合后集合添加子元素
        }

//        System.out.println(dictBOList);

        StringBuilder stringBuilder = new StringBuilder();
        String str = String.format("%-20s%-10s\n", "docId", "tf");
        stringBuilder.append(str);
        for (PositionBO positionBO : positionBOList) {
            str = String.format("%-20d%-10d\n", positionBO.getDocID(), positionBO.getTf());
            stringBuilder.append(str);
        }
        System.out.println(stringBuilder.toString());
        File file = new File(targetDirectoryPath + File.separator + "postings.txt");
        writeFile(stringBuilder.toString(), file);

        StringBuilder builder = new StringBuilder();
        String s = String.format("%-20s%-10s%-10s\n", "term", "df", "offset");
        builder.append(s);
        for (DictBO dictBO : dictBOList) {
            s = String.format("%-20s%-10s%-10s\n", dictBO.getTerm(), dictBO.getDf(), dictBO.getOffset());
            builder.append(s);
        }
        System.out.println(builder.toString());
        File file2 = new File(targetDirectoryPath + File.separator + "dictionary.txt");
        writeFile(builder.toString(), file2);

    }

    private static void writeFile(String text, File file) {
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter, 1024)
        ) {
            bufferedWriter.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getDocVec(int docid) {

        try {

            String pathIndex = "/Users/fengziyang/fuujiro/github/LuceneTutorial/indexCovid";

            // let's just retrieve the document vector (only the "text" field) for the Document with internal ID=21
            String field = "fileContent";

            Directory dir = FSDirectory.open( new File( pathIndex ).toPath() );
            IndexReader index = DirectoryReader.open( dir );

            Terms vector = index.getTermVector( docid, field ); // Read the document's document vector.

            // You need to use TermsEnum to iterate each entry of the document vector (in alphabetical order).
//            System.out.printf( "%-20s%-10s%-20s\n", "TERM", "FREQ", "POSITIONS" );
            TermsEnum terms = vector.iterator();
            PostingsEnum positions = null;
            BytesRef term;
            while ( ( term = terms.next() ) != null ) {

                String termstr = term.utf8ToString(); // Get the text string of the term.
                long freq = terms.totalTermFreq(); // Get the frequency of the term in the document.
                PositionBO position = new PositionBO();
                position.setTerm(termstr);
                position.setDocID(docid + 1);
                position.setTf(freq);
                positionBOList.add(position);
//                System.out.printf( "%-20s%-10d", termstr, freq );

                // Lucene's document vector can also provide the position of the terms
                // (in case you stored these information in the index).
                // Here you are getting a PostingsEnum that includes only one document entry, i.e., the current document.
                positions = terms.postings( positions, PostingsEnum.POSITIONS );
                positions.nextDoc(); // you still need to move the cursor
                // now accessing the occurrence position of the terms by iteratively calling nextPosition()
//                for ( int i = 0; i < freq; i++ ) {
//                    System.out.print( ( i > 0 ? "," : "" ) + positions.nextPosition() );
//                }
//                System.out.println();
            }

            index.close();
            dir.close();

        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

}
