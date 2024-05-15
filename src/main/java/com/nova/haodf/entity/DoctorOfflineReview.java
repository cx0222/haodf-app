package com.nova.haodf.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "doctor_offline_review",
        indexes = {
                @Index(name = "doctor_id_index", columnList = "doctor_id"),
                @Index(name = "type_description_index", columnList = "type_description"),
                @Index(name = "effect_index", columnList = "effect"),
                @Index(name = "attitude_index", columnList = "attitude"),
                @Index(name = "skill_index", columnList = "skill"),
                @Index(name = "real_time_index", columnList = "real_time")
        }
)
public class DoctorOfflineReview implements Serializable {
    @Id
    private Long reviewId;
    private Long doctorId;
    private Long patientId;
    private Integer status;
    private LocalDateTime realTime;
    private Integer costType;
    private Integer cost;
    private Integer agree;
    @Column(name = "reason", columnDefinition = "varchar(150)")
    private String reason;
    @Column(name = "disease", columnDefinition = "varchar(255)")
    private String disease;
    @Column(name = "type_description", columnDefinition = "varchar(4)")
    private String typeDescription;
    @Column(name = "content", columnDefinition = "varchar(40)")
    private String content;
    @Column(name = "name", columnDefinition = "varchar(100)")
    private String name;
    @Column(name = "effect", columnDefinition = "varchar(4)")
    private String effect;
    @Column(name = "attitude", columnDefinition = "varchar(4)")
    private String attitude;
    @Column(name = "skill", columnDefinition = "varchar(4)")
    private String skill;
    @Column(name = "remedy", columnDefinition = "varchar(255)")
    private String remedy;
    @Column(name = "ill_condition", columnDefinition = "varchar(50)")
    private String illCondition;
    @Column(name = "tag", columnDefinition = "varchar(255)")
    private String tag;
    @Column(name = "patient_province", columnDefinition = "varchar(20)")
    private String patientProvince;
    @Column(name = "patient_city", columnDefinition = "varchar(50)")
    private String patientCity;
    @Column(name = "attitude_description", columnDefinition = "text")
    private String attitudeDescription;
    @Column(name = "main_description", columnDefinition = "text")
    private String mainDescription;
    private Integer attitudeScore;
    private Integer mainScore;
    @Column(name = "location", columnDefinition = "varchar(20)")
    private String location;

    public DoctorOfflineReview() {
    }

    public Long getReviewId() {

        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getRealTime() {
        return realTime;
    }

    public void setRealTime(LocalDateTime realTime) {
        this.realTime = realTime;
    }

    public Integer getCostType() {
        return costType;
    }

    public void setCostType(Integer costType) {
        this.costType = costType;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getAgree() {
        return agree;
    }

    public void setAgree(Integer agree) {
        this.agree = agree;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getAttitude() {
        return attitude;
    }

    public void setAttitude(String attitude) {
        this.attitude = attitude;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getRemedy() {
        return remedy;
    }

    public void setRemedy(String remedy) {
        this.remedy = remedy;
    }

    public String getIllCondition() {
        return illCondition;
    }

    public void setIllCondition(String illCondition) {
        this.illCondition = illCondition;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPatientProvince() {
        return patientProvince;
    }

    public void setPatientProvince(String patientProvince) {
        this.patientProvince = patientProvince;
    }

    public String getPatientCity() {
        return patientCity;
    }

    public void setPatientCity(String patientCity) {
        this.patientCity = patientCity;
    }

    public String getAttitudeDescription() {
        return attitudeDescription;
    }

    public void setAttitudeDescription(String attitudeDescription) {
        this.attitudeDescription = attitudeDescription;
    }

    public String getMainDescription() {
        return mainDescription;
    }

    public void setMainDescription(String mainDescription) {
        this.mainDescription = mainDescription;
    }

    public Integer getAttitudeScore() {
        return attitudeScore;
    }

    public void setAttitudeScore(Integer attitudeScore) {
        this.attitudeScore = attitudeScore;
    }

    public Integer getMainScore() {
        return mainScore;
    }

    public void setMainScore(Integer mainScore) {
        this.mainScore = mainScore;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        DoctorOfflineReview that = (DoctorOfflineReview) object;
        return Objects.equals(reviewId, that.reviewId)
                && Objects.equals(doctorId, that.doctorId)
                && Objects.equals(patientId, that.patientId)
                && Objects.equals(status, that.status)
                && Objects.equals(realTime, that.realTime)
                && Objects.equals(costType, that.costType)
                && Objects.equals(cost, that.cost)
                && Objects.equals(agree, that.agree)
                && Objects.equals(reason, that.reason)
                && Objects.equals(disease, that.disease)
                && Objects.equals(typeDescription, that.typeDescription)
                && Objects.equals(content, that.content)
                && Objects.equals(name, that.name)
                && Objects.equals(effect, that.effect)
                && Objects.equals(attitude, that.attitude)
                && Objects.equals(skill, that.skill)
                && Objects.equals(remedy, that.remedy)
                && Objects.equals(illCondition, that.illCondition)
                && Objects.equals(tag, that.tag)
                && Objects.equals(patientProvince, that.patientProvince)
                && Objects.equals(patientCity, that.patientCity)
                && Objects.equals(attitudeDescription, that.attitudeDescription)
                && Objects.equals(mainDescription, that.mainDescription)
                && Objects.equals(attitudeScore, that.attitudeScore)
                && Objects.equals(mainScore, that.mainScore)
                && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return reviewId.hashCode();
    }

    @Override
    public String toString() {
        return "DoctorOfflineReview {" +
                "reviewId=" + reviewId +
                ", doctorId=" + doctorId +
                ", patientId=" + patientId +
                ", status=" + status +
                ", realTime=" + realTime +
                ", costType=" + costType +
                ", cost=" + cost +
                ", agree=" + agree +
                ", reason='" + reason + '\'' +
                ", disease='" + disease + '\'' +
                ", typeDescription='" + typeDescription + '\'' +
                ", content='" + content + '\'' +
                ", name='" + name + '\'' +
                ", effect='" + effect + '\'' +
                ", attitude='" + attitude + '\'' +
                ", skill='" + skill + '\'' +
                ", remedy='" + remedy + '\'' +
                ", illCondition='" + illCondition + '\'' +
                ", tag='" + tag + '\'' +
                ", patientProvince='" + patientProvince + '\'' +
                ", patientCity='" + patientCity + '\'' +
                ", attitudeDescription='" + attitudeDescription + '\'' +
                ", mainDescription='" + mainDescription + '\'' +
                ", attitudeScore=" + attitudeScore +
                ", mainScore=" + mainScore +
                ", location='" + location + '\'' +
                '}';
    }
}
