package io.github.beom.practiceboard.attachment.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;

@Builder
@Getter
public class Attachment implements Comparable<Attachment>{
    private String uuid;
    private String fileName;
    private int ord;
    private boolean img;
    private Long fileSize;
    private String contentType;
    private String domain;
    private Long referenceId;
    private LocalDateTime updatedAt;
    private Long createdBy;



    public int compareTo(Attachment other){
        return this.ord - other.ord;
    }
}
