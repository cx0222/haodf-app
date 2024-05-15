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
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "hospital",
        indexes = {
                @Index(name = "hospital_info_status_index", columnList = "hospital_info_status"),
                @Index(name = "country_rank_index", columnList = "country_rank"),
                @Index(name = "province_index", columnList = "province_name, province_rank"),
                @Index(name = "category_index", columnList = "category"),
                @Index(name = "grade_index", columnList = "grade"),
                @Index(name = "property_index", columnList = "property"),
                @Index(name = "area_code_index", columnList = "area_code")
        }
)
public class Hospital implements Serializable {
    @Id
    private Long hospitalId;
    @Nonnull
    private Integer status = EntityStatus.EMPTY;
    @NonNull
    private Integer hospitalInfoStatus = EntityStatus.EMPTY;
    @NonNull
    private Integer hospitalDetailStatus = EntityStatus.EMPTY;
    @LastModifiedDate
    private LocalDateTime lastModifiedTime;
    @Column(name = "name", columnDefinition = "varchar(30)")
    private String name;
    @Column(name = "area_code", columnDefinition = "varchar(6)")
    private String areaCode;
    @Column(name = "phone", columnDefinition = "varchar(150)")
    private String phone;
    private Integer grade;
    @Column(name = "grade_description", columnDefinition = "varchar(3)")
    private String gradeDescription;
    private Integer category;
    @Column(name = "category_description", columnDefinition = "varchar(8)")
    private String categoryDescription;
    private Integer property;
    @Column(name = "property_description", columnDefinition = "varchar(2)")
    private String propertyDescription;
    @Column(name = "introduction", columnDefinition = "varchar(12000)")
    private String introduction;
    @Column(name = "address", columnDefinition = "varchar(200)")
    private String address;
    @Column(name = "address_detail", columnDefinition = "varchar(2400)")
    private String addressDetail;
    @Column(name = "location", columnDefinition = "varchar(20)")
    private String location;
    private Integer totalFacultyCount;
    private Integer totalDoctorCount;
    private Integer totalCommentCount;
    private Integer totalDiseaseCount;
    @Column(name = "province_name", columnDefinition = "varchar(10)")
    private String provinceName;
    private Integer provinceRank;
    @Column(name = "country_name", columnDefinition = "varchar(2)")
    private String countryName;
    private Integer countryRank;
    private Integer totalSpaceHits;
    private Integer servicePatientCount;
    private Integer upVoteCount2Years;
    private Integer downVoteCount2Years;
    private Integer patientSatisfaction;
    private Integer articleCount;
    private Integer liveCount;
    private Integer yearHaodfCount;

    public Hospital() {
    }

    public Hospital(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    @Nonnull
    public Integer getStatus() {
        return status;
    }

    public void setStatus(@Nonnull Integer status) {
        this.status = status;
    }

    @NonNull
    public Integer getHospitalInfoStatus() {
        return hospitalInfoStatus;
    }

    public void setHospitalInfoStatus(@NonNull Integer hospitalInfoStatus) {
        this.hospitalInfoStatus = hospitalInfoStatus;
    }

    @NonNull
    public Integer getHospitalDetailStatus() {
        return hospitalDetailStatus;
    }

    public void setHospitalDetailStatus(@NonNull Integer hospitalDetailStatus) {
        this.hospitalDetailStatus = hospitalDetailStatus;
    }

    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getGradeDescription() {
        return gradeDescription;
    }

    public void setGradeDescription(String gradeDescription) {
        this.gradeDescription = gradeDescription;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public Integer getProperty() {
        return property;
    }

    public void setProperty(Integer property) {
        this.property = property;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getTotalFacultyCount() {
        return totalFacultyCount;
    }

    public void setTotalFacultyCount(Integer totalFacultyCount) {
        this.totalFacultyCount = totalFacultyCount;
    }

    public Integer getTotalDoctorCount() {
        return totalDoctorCount;
    }

    public void setTotalDoctorCount(Integer totalDoctorCount) {
        this.totalDoctorCount = totalDoctorCount;
    }

    public Integer getTotalCommentCount() {
        return totalCommentCount;
    }

    public void setTotalCommentCount(Integer totalCommentCount) {
        this.totalCommentCount = totalCommentCount;
    }

    public Integer getTotalDiseaseCount() {
        return totalDiseaseCount;
    }

    public void setTotalDiseaseCount(Integer totalDiseaseCount) {
        this.totalDiseaseCount = totalDiseaseCount;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Integer getProvinceRank() {
        return provinceRank;
    }

    public void setProvinceRank(Integer provinceRank) {
        this.provinceRank = provinceRank;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Integer getCountryRank() {
        return countryRank;
    }

    public void setCountryRank(Integer countryRank) {
        this.countryRank = countryRank;
    }

    public Integer getTotalSpaceHits() {
        return totalSpaceHits;
    }

    public void setTotalSpaceHits(Integer totalSpaceHits) {
        this.totalSpaceHits = totalSpaceHits;
    }

    public Integer getServicePatientCount() {
        return servicePatientCount;
    }

    public void setServicePatientCount(Integer servicePatientCount) {
        this.servicePatientCount = servicePatientCount;
    }

    public Integer getUpVoteCount2Years() {
        return upVoteCount2Years;
    }

    public void setUpVoteCount2Years(Integer upVoteCount2Years) {
        this.upVoteCount2Years = upVoteCount2Years;
    }

    public Integer getDownVoteCount2Years() {
        return downVoteCount2Years;
    }

    public void setDownVoteCount2Years(Integer downVoteCount2Years) {
        this.downVoteCount2Years = downVoteCount2Years;
    }

    public Integer getPatientSatisfaction() {
        return patientSatisfaction;
    }

    public void setPatientSatisfaction(Integer patientSatisfaction) {
        this.patientSatisfaction = patientSatisfaction;
    }

    public Integer getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Integer articleCount) {
        this.articleCount = articleCount;
    }

    public Integer getLiveCount() {
        return liveCount;
    }

    public void setLiveCount(Integer liveCount) {
        this.liveCount = liveCount;
    }

    public Integer getYearHaodfCount() {
        return yearHaodfCount;
    }

    public void setYearHaodfCount(Integer yearHaodfCount) {
        this.yearHaodfCount = yearHaodfCount;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Hospital hospital = (Hospital) object;
        return Objects.equals(hospitalId, hospital.hospitalId)
                && Objects.equals(status, hospital.status)
                && Objects.equals(hospitalInfoStatus, hospital.hospitalInfoStatus)
                && Objects.equals(hospitalDetailStatus, hospital.hospitalDetailStatus)
                && Objects.equals(lastModifiedTime, hospital.lastModifiedTime)
                && Objects.equals(name, hospital.name)
                && Objects.equals(areaCode, hospital.areaCode)
                && Objects.equals(phone, hospital.phone)
                && Objects.equals(grade, hospital.grade)
                && Objects.equals(gradeDescription, hospital.gradeDescription)
                && Objects.equals(category, hospital.category)
                && Objects.equals(categoryDescription, hospital.categoryDescription)
                && Objects.equals(property, hospital.property)
                && Objects.equals(propertyDescription, hospital.propertyDescription)
                && Objects.equals(introduction, hospital.introduction)
                && Objects.equals(address, hospital.address)
                && Objects.equals(addressDetail, hospital.addressDetail)
                && Objects.equals(location, hospital.location)
                && Objects.equals(totalFacultyCount, hospital.totalFacultyCount)
                && Objects.equals(totalDoctorCount, hospital.totalDoctorCount)
                && Objects.equals(totalCommentCount, hospital.totalCommentCount)
                && Objects.equals(totalDiseaseCount, hospital.totalDiseaseCount)
                && Objects.equals(provinceName, hospital.provinceName)
                && Objects.equals(provinceRank, hospital.provinceRank)
                && Objects.equals(countryName, hospital.countryName)
                && Objects.equals(countryRank, hospital.countryRank)
                && Objects.equals(totalSpaceHits, hospital.totalSpaceHits)
                && Objects.equals(servicePatientCount, hospital.servicePatientCount)
                && Objects.equals(upVoteCount2Years, hospital.upVoteCount2Years)
                && Objects.equals(downVoteCount2Years, hospital.downVoteCount2Years)
                && Objects.equals(patientSatisfaction, hospital.patientSatisfaction)
                && Objects.equals(articleCount, hospital.articleCount)
                && Objects.equals(liveCount, hospital.liveCount)
                && Objects.equals(yearHaodfCount, hospital.yearHaodfCount);
    }

    @Override
    public int hashCode() {
        return hospitalId.hashCode();
    }

    @Override
    public String toString() {
        return "Hospital {" +
                "hospitalId=" + hospitalId +
                ", status=" + status +
                ", hospitalInfoStatus=" + hospitalInfoStatus +
                ", hospitalDetailStatus=" + hospitalDetailStatus +
                ", lastModifiedTime=" + lastModifiedTime +
                ", name='" + name + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", phone='" + phone + '\'' +
                ", grade=" + grade +
                ", gradeDescription='" + gradeDescription + '\'' +
                ", category=" + category +
                ", categoryDescription='" + categoryDescription + '\'' +
                ", property=" + property +
                ", propertyDescription='" + propertyDescription + '\'' +
                ", introduction='" + introduction + '\'' +
                ", address='" + address + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", location='" + location + '\'' +
                ", totalFacultyCount=" + totalFacultyCount +
                ", totalDoctorCount=" + totalDoctorCount +
                ", totalCommentCount=" + totalCommentCount +
                ", totalDiseaseCount=" + totalDiseaseCount +
                ", provinceName='" + provinceName + '\'' +
                ", provinceRank=" + provinceRank +
                ", countryName='" + countryName + '\'' +
                ", countryRank=" + countryRank +
                ", totalSpaceHits=" + totalSpaceHits +
                ", servicePatientCount=" + servicePatientCount +
                ", upVoteCount2Years=" + upVoteCount2Years +
                ", downVoteCount2Years=" + downVoteCount2Years +
                ", patientSatisfaction=" + patientSatisfaction +
                ", articleCount=" + articleCount +
                ", liveCount=" + liveCount +
                ", yearHaodfCount=" + yearHaodfCount +
                '}';
    }
}
