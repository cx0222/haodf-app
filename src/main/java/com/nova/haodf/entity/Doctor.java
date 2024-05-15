package com.nova.haodf.entity;

import com.nova.haodf.util.EntityStatus;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "doctor",
        indexes = {
                @Index(name = "hot_rank_index", columnList = "hot_rank"),
                @Index(name = "recommend_index_index", columnList = "recommend_index"),
                @Index(name = "article_count_index", columnList = "article_count"),
                @Index(name = "doctor_vote_count_index", columnList = "doctor_vote_count"),
                @Index(name = "open_space_time_index", columnList = "open_space_time"),
                @Index(name = "present_count_index", columnList = "present_count"),
                @Index(name = "space_replied_count_index", columnList = "space_replied_count"),
                @Index(name = "total_hit_index", columnList = "total_hit"),
                @Index(name = "total_sign_in_count_index", columnList = "total_sign_in_count"),
                @Index(name = "vote_in2years_index", columnList = "vote_in2years"),
                @Index(name = "area_code_index", columnList = "area_code")
        }
)
public class Doctor implements Serializable {
    @Id
    private Long doctorId;
    @Nonnull
    private Long hospitalId = 0L;
    @Nonnull
    private Integer offlineStatus = EntityStatus.EMPTY;
    @Nonnull
    private Integer onlineStatus = EntityStatus.EMPTY;
    @Nonnull
    private Integer consultationStatus = EntityStatus.EMPTY;
    @Nonnull
    private Integer doctorDetailStatus = EntityStatus.EMPTY;
    @Nonnull
    private Integer doctorStatisticsStatus = EntityStatus.EMPTY;
    @LastModifiedDate
    private LocalDateTime lastModifiedTime;
    @Column(name = "grade", columnDefinition = "varchar(10)")
    private String grade;
    @Column(name = "name", columnDefinition = "varchar(30)")
    private String name;
    @Column(name = "education_grade", columnDefinition = "varchar(4)")
    private String educationGrade;
    @Column(name = "title", columnDefinition = "varchar(20)")
    private String title;
    @Column(name = "specialization", columnDefinition = "varchar(2400)")
    private String specialization;
    @Column(name = "province", columnDefinition = "varchar(10)")
    private String province;
    @Column(name = "city", columnDefinition = "varchar(10)")
    private String city;
    @Column(name = "district", columnDefinition = "varchar(30)")
    private String district;
    @Column(name = "area_code", columnDefinition = "varchar(6)")
    private String areaCode;
    private Long facultyId;
    @Column(name = "faculty_name", columnDefinition = "varchar(10)")
    private String facultyName;
    private Long hospitalFacultyId;
    @Column(name = "hospital_faculty_name", columnDefinition = "varchar(30)")
    private String hospitalFacultyName;
    private Long spaceId;
    @Column(name = "doctor_introduction", columnDefinition = "text")
    private String doctorIntroduction;
    @Column(name = "head_image", columnDefinition = "varchar(20)")
    private String headImage;
    @Column(name = "head_image_thumbnail", columnDefinition = "varchar(100)")
    private String headImageThumbnail;
    private Integer isHidden;
    @Column(name = "schedule", columnDefinition = "varchar(20)")
    private String schedule;
    @Column(name = "medal_honor_year", columnDefinition = "varchar(10)")
    private String medalHonorYear;
    @Column(name = "medal_honor_url", columnDefinition = "varchar(100)")
    private String medalHonorUrl;
    @Column(name = "medal_honor_url_for_new", columnDefinition = "varchar(60)")
    private String medalHonorUrlForNew;
    @Column(name = "hospital_character", columnDefinition = "varchar(10)")
    private String hospitalCharacter;
    @Column(name = "primary_hospital_character", columnDefinition = "varchar(10)")
    private String primaryHospitalCharacter;
    private Integer counterpartsRank;
    private Double hotRank;
    private Integer complexRank;
    private Integer recommendStatus;
    private Double recommendIndex;
    private Integer isRegisterOpened;
    @Column(name = "register_description", columnDefinition = "varchar(3)")
    private String registerDescription;
    private Integer isOnlineClinicOpened;
    @Column(name = "online_clinic_description", columnDefinition = "varchar(10)")
    private String onlineClinicDescription;
    private Integer isCaseOpened;
    private Integer isPhoneOpened;
    private Integer isBookingOpened;
    private Integer isServiceStar;
    private Integer isOpenRemoteClinic;
    private Integer isOpenSurgery;
    private Integer isOpenFamilyDoctor;
    private Integer isOpenCosmeToLogVideo;
    private Integer textBasedConsultationPrice;
    private Integer isTextBasedConsultationOpened;
    @Column(name = "text_based_consultation_description", columnDefinition = "varchar(4)")
    private String textBasedConsultationDescription;
    private Integer phoneBasedConsultationPrice;
    private Integer isPhoneBasedConsultationOpened;
    @Column(name = "phone_based_consultation_description", columnDefinition = "varchar(4)")
    private String phoneBasedConsultationDescription;
    private Integer skill;
    private Integer attitude;
    private Integer articleCount;
    private Integer doctorVoteCount;
    private LocalDateTime openSpaceTime;
    private Integer presentCount;
    private Integer spaceRepliedCount;
    private Integer totalHit;
    private Integer totalSignInCount;
    private Integer voteIn2Years;

    public Doctor() {
    }

    public Doctor(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    @Nonnull
    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(@Nonnull Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    @Nonnull
    public Integer getOfflineStatus() {
        return offlineStatus;
    }

    public void setOfflineStatus(@Nonnull Integer offlineStatus) {
        this.offlineStatus = offlineStatus;
    }

    @Nonnull
    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(@Nonnull Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    @Nonnull
    public Integer getConsultationStatus() {
        return consultationStatus;
    }

    public void setConsultationStatus(@Nonnull Integer consultationStatus) {
        this.consultationStatus = consultationStatus;
    }

    @Nonnull
    public Integer getDoctorDetailStatus() {
        return doctorDetailStatus;
    }

    public void setDoctorDetailStatus(@Nonnull Integer doctorDetailStatus) {
        this.doctorDetailStatus = doctorDetailStatus;
    }

    @Nonnull
    public Integer getDoctorStatisticsStatus() {
        return doctorStatisticsStatus;
    }

    public void setDoctorStatisticsStatus(@Nonnull Integer doctorStatisticsStatus) {
        this.doctorStatisticsStatus = doctorStatisticsStatus;
    }

    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEducationGrade() {
        return educationGrade;
    }

    public void setEducationGrade(String educationGrade) {
        this.educationGrade = educationGrade;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public Long getHospitalFacultyId() {
        return hospitalFacultyId;
    }

    public void setHospitalFacultyId(Long hospitalFacultyId) {
        this.hospitalFacultyId = hospitalFacultyId;
    }

    public String getHospitalFacultyName() {
        return hospitalFacultyName;
    }

    public void setHospitalFacultyName(String hospitalFacultyName) {
        this.hospitalFacultyName = hospitalFacultyName;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public String getDoctorIntroduction() {
        return doctorIntroduction;
    }

    public void setDoctorIntroduction(String doctorIntroduction) {
        this.doctorIntroduction = doctorIntroduction;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getHeadImageThumbnail() {
        return headImageThumbnail;
    }

    public void setHeadImageThumbnail(String headImageThumbnail) {
        this.headImageThumbnail = headImageThumbnail;
    }

    public Integer getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Integer isHidden) {
        this.isHidden = isHidden;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getMedalHonorYear() {
        return medalHonorYear;
    }

    public void setMedalHonorYear(String medalHonorYear) {
        this.medalHonorYear = medalHonorYear;
    }

    public String getMedalHonorUrl() {
        return medalHonorUrl;
    }

    public void setMedalHonorUrl(String medalHonorUrl) {
        this.medalHonorUrl = medalHonorUrl;
    }

    public String getMedalHonorUrlForNew() {
        return medalHonorUrlForNew;
    }

    public void setMedalHonorUrlForNew(String medalHonorUrlForNew) {
        this.medalHonorUrlForNew = medalHonorUrlForNew;
    }

    public String getHospitalCharacter() {
        return hospitalCharacter;
    }

    public void setHospitalCharacter(String hospitalCharacter) {
        this.hospitalCharacter = hospitalCharacter;
    }

    public String getPrimaryHospitalCharacter() {
        return primaryHospitalCharacter;
    }

    public void setPrimaryHospitalCharacter(String primaryHospitalCharacter) {
        this.primaryHospitalCharacter = primaryHospitalCharacter;
    }

    public Integer getCounterpartsRank() {
        return counterpartsRank;
    }

    public void setCounterpartsRank(Integer counterpartsRank) {
        this.counterpartsRank = counterpartsRank;
    }

    public Double getHotRank() {
        return hotRank;
    }

    public void setHotRank(Double hotRank) {
        this.hotRank = hotRank;
    }

    public Integer getComplexRank() {
        return complexRank;
    }

    public void setComplexRank(Integer complexRank) {
        this.complexRank = complexRank;
    }

    public Integer getRecommendStatus() {
        return recommendStatus;
    }

    public void setRecommendStatus(Integer recommendStatus) {
        this.recommendStatus = recommendStatus;
    }

    public Double getRecommendIndex() {
        return recommendIndex;
    }

    public void setRecommendIndex(Double recommendIndex) {
        this.recommendIndex = recommendIndex;
    }

    public Integer getIsRegisterOpened() {
        return isRegisterOpened;
    }

    public void setIsRegisterOpened(Integer isRegisterOpened) {
        this.isRegisterOpened = isRegisterOpened;
    }

    public String getRegisterDescription() {
        return registerDescription;
    }

    public void setRegisterDescription(String registerDescription) {
        this.registerDescription = registerDescription;
    }

    public Integer getIsOnlineClinicOpened() {
        return isOnlineClinicOpened;
    }

    public void setIsOnlineClinicOpened(Integer isOnlineClinicOpened) {
        this.isOnlineClinicOpened = isOnlineClinicOpened;
    }

    public String getOnlineClinicDescription() {
        return onlineClinicDescription;
    }

    public void setOnlineClinicDescription(String onlineClinicDescription) {
        this.onlineClinicDescription = onlineClinicDescription;
    }

    public Integer getIsCaseOpened() {
        return isCaseOpened;
    }

    public void setIsCaseOpened(Integer isCaseOpened) {
        this.isCaseOpened = isCaseOpened;
    }

    public Integer getIsPhoneOpened() {
        return isPhoneOpened;
    }

    public void setIsPhoneOpened(Integer isPhoneOpened) {
        this.isPhoneOpened = isPhoneOpened;
    }

    public Integer getIsBookingOpened() {
        return isBookingOpened;
    }

    public void setIsBookingOpened(Integer isBookingOpened) {
        this.isBookingOpened = isBookingOpened;
    }

    public Integer getIsServiceStar() {
        return isServiceStar;
    }

    public void setIsServiceStar(Integer isServiceStar) {
        this.isServiceStar = isServiceStar;
    }

    public Integer getIsOpenRemoteClinic() {
        return isOpenRemoteClinic;
    }

    public void setIsOpenRemoteClinic(Integer isOpenRemoteClinic) {
        this.isOpenRemoteClinic = isOpenRemoteClinic;
    }

    public Integer getIsOpenSurgery() {
        return isOpenSurgery;
    }

    public void setIsOpenSurgery(Integer isOpenSurgery) {
        this.isOpenSurgery = isOpenSurgery;
    }

    public Integer getIsOpenFamilyDoctor() {
        return isOpenFamilyDoctor;
    }

    public void setIsOpenFamilyDoctor(Integer isOpenFamilyDoctor) {
        this.isOpenFamilyDoctor = isOpenFamilyDoctor;
    }

    public Integer getIsOpenCosmeToLogVideo() {
        return isOpenCosmeToLogVideo;
    }

    public void setIsOpenCosmeToLogVideo(Integer isOpenCosmeToLogVideo) {
        this.isOpenCosmeToLogVideo = isOpenCosmeToLogVideo;
    }

    public Integer getTextBasedConsultationPrice() {
        return textBasedConsultationPrice;
    }

    public void setTextBasedConsultationPrice(Integer textBasedConsultationPrice) {
        this.textBasedConsultationPrice = textBasedConsultationPrice;
    }

    public Integer getIsTextBasedConsultationOpened() {
        return isTextBasedConsultationOpened;
    }

    public void setIsTextBasedConsultationOpened(Integer isTextBasedConsultationOpened) {
        this.isTextBasedConsultationOpened = isTextBasedConsultationOpened;
    }

    public String getTextBasedConsultationDescription() {
        return textBasedConsultationDescription;
    }

    public void setTextBasedConsultationDescription(String textBasedConsultationDescription) {
        this.textBasedConsultationDescription = textBasedConsultationDescription;
    }

    public Integer getPhoneBasedConsultationPrice() {
        return phoneBasedConsultationPrice;
    }

    public void setPhoneBasedConsultationPrice(Integer phoneBasedConsultationPrice) {
        this.phoneBasedConsultationPrice = phoneBasedConsultationPrice;
    }

    public Integer getIsPhoneBasedConsultationOpened() {
        return isPhoneBasedConsultationOpened;
    }

    public void setIsPhoneBasedConsultationOpened(Integer isPhoneBasedConsultationOpened) {
        this.isPhoneBasedConsultationOpened = isPhoneBasedConsultationOpened;
    }

    public String getPhoneBasedConsultationDescription() {
        return phoneBasedConsultationDescription;
    }

    public void setPhoneBasedConsultationDescription(String phoneBasedConsultationDescription) {
        this.phoneBasedConsultationDescription = phoneBasedConsultationDescription;
    }

    public Integer getSkill() {
        return skill;
    }

    public void setSkill(Integer skill) {
        this.skill = skill;
    }

    public Integer getAttitude() {
        return attitude;
    }

    public void setAttitude(Integer attitude) {
        this.attitude = attitude;
    }

    public Integer getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Integer articleCount) {
        this.articleCount = articleCount;
    }

    public Integer getDoctorVoteCount() {
        return doctorVoteCount;
    }

    public void setDoctorVoteCount(Integer doctorVoteCount) {
        this.doctorVoteCount = doctorVoteCount;
    }

    public LocalDateTime getOpenSpaceTime() {
        return openSpaceTime;
    }

    public void setOpenSpaceTime(LocalDateTime openSpaceTime) {
        this.openSpaceTime = openSpaceTime;
    }

    public Integer getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(Integer presentCount) {
        this.presentCount = presentCount;
    }

    public Integer getSpaceRepliedCount() {
        return spaceRepliedCount;
    }

    public void setSpaceRepliedCount(Integer spaceRepliedCount) {
        this.spaceRepliedCount = spaceRepliedCount;
    }

    public Integer getTotalHit() {
        return totalHit;
    }

    public void setTotalHit(Integer totalHit) {
        this.totalHit = totalHit;
    }

    public Integer getTotalSignInCount() {
        return totalSignInCount;
    }

    public void setTotalSignInCount(Integer totalSignInCount) {
        this.totalSignInCount = totalSignInCount;
    }

    public Integer getVoteIn2Years() {
        return voteIn2Years;
    }

    public void setVoteIn2Years(Integer voteIn2Years) {
        this.voteIn2Years = voteIn2Years;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Doctor doctor = (Doctor) object;
        return Objects.equals(doctorId, doctor.doctorId)
                && Objects.equals(hospitalId, doctor.hospitalId)
                && Objects.equals(offlineStatus, doctor.offlineStatus)
                && Objects.equals(onlineStatus, doctor.onlineStatus)
                && Objects.equals(consultationStatus, doctor.consultationStatus)
                && Objects.equals(doctorDetailStatus, doctor.doctorDetailStatus)
                && Objects.equals(doctorStatisticsStatus, doctor.doctorStatisticsStatus)
                && Objects.equals(lastModifiedTime, doctor.lastModifiedTime)
                && Objects.equals(grade, doctor.grade)
                && Objects.equals(name, doctor.name)
                && Objects.equals(educationGrade, doctor.educationGrade)
                && Objects.equals(title, doctor.title)
                && Objects.equals(specialization, doctor.specialization)
                && Objects.equals(province, doctor.province)
                && Objects.equals(city, doctor.city)
                && Objects.equals(district, doctor.district)
                && Objects.equals(areaCode, doctor.areaCode)
                && Objects.equals(facultyId, doctor.facultyId)
                && Objects.equals(facultyName, doctor.facultyName)
                && Objects.equals(hospitalFacultyId, doctor.hospitalFacultyId)
                && Objects.equals(hospitalFacultyName, doctor.hospitalFacultyName)
                && Objects.equals(spaceId, doctor.spaceId)
                && Objects.equals(doctorIntroduction, doctor.doctorIntroduction)
                && Objects.equals(headImage, doctor.headImage)
                && Objects.equals(headImageThumbnail, doctor.headImageThumbnail)
                && Objects.equals(isHidden, doctor.isHidden)
                && Objects.equals(schedule, doctor.schedule)
                && Objects.equals(medalHonorYear, doctor.medalHonorYear)
                && Objects.equals(medalHonorUrl, doctor.medalHonorUrl)
                && Objects.equals(medalHonorUrlForNew, doctor.medalHonorUrlForNew)
                && Objects.equals(hospitalCharacter, doctor.hospitalCharacter)
                && Objects.equals(primaryHospitalCharacter, doctor.primaryHospitalCharacter)
                && Objects.equals(counterpartsRank, doctor.counterpartsRank)
                && Objects.equals(hotRank, doctor.hotRank)
                && Objects.equals(complexRank, doctor.complexRank)
                && Objects.equals(recommendStatus, doctor.recommendStatus)
                && Objects.equals(recommendIndex, doctor.recommendIndex)
                && Objects.equals(isRegisterOpened, doctor.isRegisterOpened)
                && Objects.equals(registerDescription, doctor.registerDescription)
                && Objects.equals(isOnlineClinicOpened, doctor.isOnlineClinicOpened)
                && Objects.equals(onlineClinicDescription, doctor.onlineClinicDescription)
                && Objects.equals(isCaseOpened, doctor.isCaseOpened)
                && Objects.equals(isPhoneOpened, doctor.isPhoneOpened)
                && Objects.equals(isBookingOpened, doctor.isBookingOpened)
                && Objects.equals(isServiceStar, doctor.isServiceStar)
                && Objects.equals(isOpenRemoteClinic, doctor.isOpenRemoteClinic)
                && Objects.equals(isOpenSurgery, doctor.isOpenSurgery)
                && Objects.equals(isOpenFamilyDoctor, doctor.isOpenFamilyDoctor)
                && Objects.equals(isOpenCosmeToLogVideo, doctor.isOpenCosmeToLogVideo)
                && Objects.equals(textBasedConsultationPrice, doctor.textBasedConsultationPrice)
                && Objects.equals(isTextBasedConsultationOpened, doctor.isTextBasedConsultationOpened)
                && Objects.equals(textBasedConsultationDescription, doctor.textBasedConsultationDescription)
                && Objects.equals(phoneBasedConsultationPrice, doctor.phoneBasedConsultationPrice)
                && Objects.equals(isPhoneBasedConsultationOpened, doctor.isPhoneBasedConsultationOpened)
                && Objects.equals(phoneBasedConsultationDescription, doctor.phoneBasedConsultationDescription)
                && Objects.equals(skill, doctor.skill)
                && Objects.equals(attitude, doctor.attitude)
                && Objects.equals(articleCount, doctor.articleCount)
                && Objects.equals(doctorVoteCount, doctor.doctorVoteCount)
                && Objects.equals(openSpaceTime, doctor.openSpaceTime)
                && Objects.equals(presentCount, doctor.presentCount)
                && Objects.equals(spaceRepliedCount, doctor.spaceRepliedCount)
                && Objects.equals(totalHit, doctor.totalHit)
                && Objects.equals(totalSignInCount, doctor.totalSignInCount)
                && Objects.equals(voteIn2Years, doctor.voteIn2Years);
    }

    @Override
    public int hashCode() {
        return hospitalId.hashCode();
    }

    @Override
    public String toString() {
        return "Doctor {" +
                "doctorId=" + doctorId +
                ", hospitalId=" + hospitalId +
                ", offlineStatus=" + offlineStatus +
                ", onlineStatus=" + onlineStatus +
                ", consultationStatus=" + consultationStatus +
                ", doctorDetailStatus=" + doctorDetailStatus +
                ", doctorStatisticsStatus=" + doctorStatisticsStatus +
                ", lastModifiedTime=" + lastModifiedTime +
                ", grade='" + grade + '\'' +
                ", name='" + name + '\'' +
                ", educationGrade='" + educationGrade + '\'' +
                ", title='" + title + '\'' +
                ", specialization='" + specialization + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", facultyId=" + facultyId +
                ", facultyName='" + facultyName + '\'' +
                ", hospitalFacultyId=" + hospitalFacultyId +
                ", hospitalFacultyName='" + hospitalFacultyName + '\'' +
                ", spaceId=" + spaceId +
                ", doctorIntroduction='" + doctorIntroduction + '\'' +
                ", headImage='" + headImage + '\'' +
                ", headImageThumbnail='" + headImageThumbnail + '\'' +
                ", isHidden=" + isHidden +
                ", schedule='" + schedule + '\'' +
                ", medalHonorYear='" + medalHonorYear + '\'' +
                ", medalHonorUrl='" + medalHonorUrl + '\'' +
                ", medalHonorUrlForNew='" + medalHonorUrlForNew + '\'' +
                ", hospitalCharacter='" + hospitalCharacter + '\'' +
                ", primaryHospitalCharacter='" + primaryHospitalCharacter + '\'' +
                ", counterpartsRank=" + counterpartsRank +
                ", hotRank=" + hotRank +
                ", complexRank=" + complexRank +
                ", recommendStatus=" + recommendStatus +
                ", recommendIndex=" + recommendIndex +
                ", isRegisterOpened=" + isRegisterOpened +
                ", registerDescription='" + registerDescription + '\'' +
                ", isOnlineClinicOpened=" + isOnlineClinicOpened +
                ", onlineClinicDescription='" + onlineClinicDescription + '\'' +
                ", isCaseOpened=" + isCaseOpened +
                ", isPhoneOpened=" + isPhoneOpened +
                ", isBookingOpened=" + isBookingOpened +
                ", isServiceStar=" + isServiceStar +
                ", isOpenRemoteClinic=" + isOpenRemoteClinic +
                ", isOpenSurgery=" + isOpenSurgery +
                ", isOpenFamilyDoctor=" + isOpenFamilyDoctor +
                ", isOpenCosmeToLogVideo=" + isOpenCosmeToLogVideo +
                ", textBasedConsultationPrice=" + textBasedConsultationPrice +
                ", isTextBasedConsultationOpened=" + isTextBasedConsultationOpened +
                ", textBasedConsultationDescription='" + textBasedConsultationDescription + '\'' +
                ", phoneBasedConsultationPrice=" + phoneBasedConsultationPrice +
                ", isPhoneBasedConsultationOpened=" + isPhoneBasedConsultationOpened +
                ", phoneBasedConsultationDescription='" + phoneBasedConsultationDescription + '\'' +
                ", skill=" + skill +
                ", attitude=" + attitude +
                ", articleCount=" + articleCount +
                ", doctorVoteCount=" + doctorVoteCount +
                ", openSpaceTime=" + openSpaceTime +
                ", presentCount=" + presentCount +
                ", spaceRepliedCount=" + spaceRepliedCount +
                ", totalHit=" + totalHit +
                ", totalSignInCount=" + totalSignInCount +
                ", voteIn2Years=" + voteIn2Years +
                '}';
    }
}
