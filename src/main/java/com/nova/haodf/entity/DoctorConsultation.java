package com.nova.haodf.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "doctor_consultation",
        indexes = {
                @Index(name = "doctor_id_index", columnList = "doctor_id"),
                @Index(name = "patient_id_index", columnList = "patient_id"),
                @Index(name = "business_type_index", columnList = "business_type"),
                @Index(name = "message_count_index", columnList = "message_count"),
                @Index(name = "doctor_message_count_index", columnList = "doctor_message_count"),
                @Index(name = "patient_index", columnList = "patient_age, patient_gender"),
                @Index(name = "start_date_index", columnList = "start_date")
        }
)
public class DoctorConsultation implements Serializable {
    @Id
    private Long consultationId;
    private Long doctorId;
    private Long patientId;
    private Integer status;
    private String title;
    private String diseaseName;
    @Column(name = "business_type", columnDefinition = "varchar(20)")
    private String businessType;
    private Integer messageCount;
    private Integer doctorMessageCount;
    private Integer hasPrescription;
    private Integer hasMedicalSummary;
    private Integer isGoodConsultation;
    private LocalDate startDate;
    @Column(name = "message_text", columnDefinition = "text")
    private String messageText;
    private Long diseaseId;
    @Column(name = "term_name", columnDefinition = "varchar(20)")
    private String termName;
    @Column(name = "patient_gender", columnDefinition = "varchar(2)")
    private String patientGender;
    private Integer patientAge;

    public DoctorConsultation() {
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Long consultationId) {
        this.consultationId = consultationId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public Integer getDoctorMessageCount() {
        return doctorMessageCount;
    }

    public void setDoctorMessageCount(Integer doctorMessageCount) {
        this.doctorMessageCount = doctorMessageCount;
    }

    public Integer getHasPrescription() {
        return hasPrescription;
    }

    public void setHasPrescription(Integer hasPrescription) {
        this.hasPrescription = hasPrescription;
    }

    public Integer getHasMedicalSummary() {
        return hasMedicalSummary;
    }

    public void setHasMedicalSummary(Integer hasMedicalSummary) {
        this.hasMedicalSummary = hasMedicalSummary;
    }

    public Integer getIsGoodConsultation() {
        return isGoodConsultation;
    }

    public void setIsGoodConsultation(Integer isGoodConsultation) {
        this.isGoodConsultation = isGoodConsultation;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Long getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(Long diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public Integer getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(Integer patientAge) {
        this.patientAge = patientAge;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        DoctorConsultation that = (DoctorConsultation) object;
        return Objects.equals(consultationId, that.consultationId)
                && Objects.equals(doctorId, that.doctorId)
                && Objects.equals(patientId, that.patientId)
                && Objects.equals(status, that.status)
                && Objects.equals(title, that.title)
                && Objects.equals(diseaseName, that.diseaseName)
                && Objects.equals(businessType, that.businessType)
                && Objects.equals(messageCount, that.messageCount)
                && Objects.equals(doctorMessageCount, that.doctorMessageCount)
                && Objects.equals(hasPrescription, that.hasPrescription)
                && Objects.equals(hasMedicalSummary, that.hasMedicalSummary)
                && Objects.equals(isGoodConsultation, that.isGoodConsultation)
                && Objects.equals(startDate, that.startDate)
                && Objects.equals(messageText, that.messageText)
                && Objects.equals(diseaseId, that.diseaseId)
                && Objects.equals(termName, that.termName)
                && Objects.equals(patientGender, that.patientGender)
                && Objects.equals(patientAge, that.patientAge);
    }

    @Override
    public int hashCode() {
        return consultationId.hashCode();
    }

    @Override
    public String toString() {
        return "DoctorConsultation {" +
                "consultationId=" + consultationId +
                ", doctorId=" + doctorId +
                ", patientId=" + patientId +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", diseaseName='" + diseaseName + '\'' +
                ", businessType='" + businessType + '\'' +
                ", messageCount=" + messageCount +
                ", doctorMessageCount=" + doctorMessageCount +
                ", hasPrescription=" + hasPrescription +
                ", hasMedicalSummary=" + hasMedicalSummary +
                ", isGoodConsultation=" + isGoodConsultation +
                ", startDate=" + startDate +
                ", messageText='" + messageText + '\'' +
                ", diseaseId=" + diseaseId +
                ", termName=" + termName +
                ", patientGender='" + patientGender + '\'' +
                ", patientAge=" + patientAge +
                '}';
    }
}
