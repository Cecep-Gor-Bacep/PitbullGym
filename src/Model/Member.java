package Model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Member {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty phone;
    private final StringProperty planType;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;
    private final StringProperty status;

    // Constructor
    public Member(int id, String name, String phone, String planType,
                  LocalDate startDate, LocalDate endDate) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.phone = new SimpleStringProperty(phone);
        this.planType = new SimpleStringProperty(planType);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.status = new SimpleStringProperty(calculateStatus(endDate));
    }

    // Calculate status based on end date
    private String calculateStatus(LocalDate endDate) {
        if (endDate == null) return "Unknown";
        LocalDate today = LocalDate.now();
        return endDate.isAfter(today) || endDate.isEqual(today) ? "Active" : "Expired";
    }

    // Getters and Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getPlanType() {
        return planType.get();
    }

    public void setPlanType(String planType) {
        this.planType.set(planType);
    }

    public StringProperty planTypeProperty() {
        return planType;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
        updateStatus();
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    // Update status when end date changes
    public void updateStatus() {
        this.status.set(calculateStatus(this.endDate.get()));
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id.get() +
                ", name='" + name.get() + '\'' +
                ", phone='" + phone.get() + '\'' +
                ", planType='" + planType.get() + '\'' +
                ", startDate=" + startDate.get() +
                ", endDate=" + endDate.get() +
                ", status='" + status.get() + '\'' +
                '}';
    }
}