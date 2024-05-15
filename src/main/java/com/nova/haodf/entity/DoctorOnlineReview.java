package com.nova.haodf.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "doctor_online_review",
        indexes = {
                @Index(name = "doctor_id_index", columnList = "doctor_id"),
                @Index(name = "consultation_id_index", columnList = "consultation_id"),
                @Index(name = "evaluate_date_index", columnList = "evaluate_date")
        }
)
public class DoctorOnlineReview implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "online_review_id", insertable = false, updatable = false, nullable = false)
    private UUID onlineReviewId;
    private Long doctorId;
    private Long consultationId;
    private Integer status;
    @Column(name = "patient_name", columnDefinition = "varchar(10)")
    private String patientName;
    @Column(name = "disease_tag", columnDefinition = "varchar(20)")
    private String diseaseTag;
    private LocalDateTime evaluateDate;
    @Column(name = "trait", columnDefinition = "varchar(50)")
    private String trait;
    @Column(name = "detail", columnDefinition = "varchar(255)")
    private String detail;
    @Column(name = "more_detail_url", columnDefinition = "varchar(50)")
    private String moreDetailUrl;
    @Column(name = "trait_satisfaction")
    private Integer traitSatisfaction;

    public DoctorOnlineReview() {
    }

    public UUID getOnlineReviewId() {
        return onlineReviewId;
    }

    public void setOnlineReviewId(UUID onlineReviewId) {
        this.onlineReviewId = onlineReviewId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Long consultationId) {
        this.consultationId = consultationId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDiseaseTag() {
        return diseaseTag;
    }

    public void setDiseaseTag(String diseaseTag) {
        this.diseaseTag = diseaseTag;
    }

    public LocalDateTime getEvaluateDate() {
        return evaluateDate;
    }

    public void setEvaluateDate(LocalDateTime evaluateDate) {
        this.evaluateDate = evaluateDate;
    }

    public String getTrait() {
        return trait;
    }

    public void setTrait(String trait) {
        this.trait = trait;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getMoreDetailUrl() {
        return moreDetailUrl;
    }

    public void setMoreDetailUrl(String moreDetailUrl) {
        this.moreDetailUrl = moreDetailUrl;
    }

    public Integer getTraitSatisfaction() {
        return traitSatisfaction;
    }

    public void setTraitSatisfaction(Integer traitSatisfaction) {
        this.traitSatisfaction = traitSatisfaction;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        DoctorOnlineReview that = (DoctorOnlineReview) object;
        return Objects.equals(onlineReviewId, that.onlineReviewId)
                && Objects.equals(doctorId, that.doctorId)
                && Objects.equals(consultationId, that.consultationId)
                && Objects.equals(status, that.status)
                && Objects.equals(patientName, that.patientName)
                && Objects.equals(diseaseTag, that.diseaseTag)
                && Objects.equals(evaluateDate, that.evaluateDate)
                && Objects.equals(trait, that.trait)
                && Objects.equals(detail, that.detail)
                && Objects.equals(moreDetailUrl, that.moreDetailUrl)
                && Objects.equals(traitSatisfaction, that.traitSatisfaction);
    }

    @Override
    public int hashCode() {
        return onlineReviewId.hashCode();
    }

    @Override
    public String toString() {
        return "DoctorOnlineReview {" +
                "onlineReviewId=" + onlineReviewId +
                ", doctorId=" + doctorId +
                ", consultationId=" + consultationId +
                ", status=" + status +
                ", patientName='" + patientName + '\'' +
                ", diseaseTag='" + diseaseTag + '\'' +
                ", evaluateDate=" + evaluateDate +
                ", trait='" + trait + '\'' +
                ", detail='" + detail + '\'' +
                ", moreDetailUrl='" + moreDetailUrl + '\'' +
                ", traitSatisfaction=" + traitSatisfaction +
                '}';
    }
}
