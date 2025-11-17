package Model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class DashboardData {
    private final ObjectProperty<LocalDate> date;
    private final IntegerProperty visitorCount;
    private final IntegerProperty productsSold;
    private final LongProperty dailyIncome;

    // Constants
    public static final int DAILY_GYM_FEE = 10000; // Rp 10.000 per visitor

    public DashboardData() {
        this.date = new SimpleObjectProperty<>(LocalDate.now());
        this.visitorCount = new SimpleIntegerProperty(0);
        this.productsSold = new SimpleIntegerProperty(0);
        this.dailyIncome = new SimpleLongProperty(0);
    }

    public DashboardData(LocalDate date, int visitorCount, int productsSold, long dailyIncome) {
        this.date = new SimpleObjectProperty<>(date);
        this.visitorCount = new SimpleIntegerProperty(visitorCount);
        this.productsSold = new SimpleIntegerProperty(productsSold);
        this.dailyIncome = new SimpleLongProperty(dailyIncome);
    }

    // Add visitor (automatically adds income)
    public void addVisitor() {
        visitorCount.set(visitorCount.get() + 1);
        dailyIncome.set(dailyIncome.get() + DAILY_GYM_FEE);
    }

    // Add product sale (with price)
    public void addProductSale(long productPrice) {
        productsSold.set(productsSold.get() + 1);
        dailyIncome.set(dailyIncome.get() + productPrice);
    }

    // Add custom income
    public void addIncome(long amount) {
        dailyIncome.set(dailyIncome.get() + amount);
    }

    // Reset daily data
    public void resetDailyData() {
        visitorCount.set(0);
        productsSold.set(0);
        dailyIncome.set(0);
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public int getVisitorCount() {
        return visitorCount.get();
    }

    public void setVisitorCount(int count) {
        this.visitorCount.set(count);
    }

    public IntegerProperty visitorCountProperty() {
        return visitorCount;
    }

    public int getProductsSold() {
        return productsSold.get();
    }

    public void setProductsSold(int count) {
        this.productsSold.set(count);
    }

    public IntegerProperty productsSoldProperty() {
        return productsSold;
    }

    public long getDailyIncome() {
        return dailyIncome.get();
    }

    public void setDailyIncome(long income) {
        this.dailyIncome.set(income);
    }

    public LongProperty dailyIncomeProperty() {
        return dailyIncome;
    }

    // Format currency
    public String getFormattedIncome() {
        return String.format("Rp %,d", dailyIncome.get());
    }

    @Override
    public String toString() {
        return "DashboardData{" +
                "date=" + date.get() +
                ", visitorCount=" + visitorCount.get() +
                ", productsSold=" + productsSold.get() +
                ", dailyIncome=Rp " + String.format("%,d", dailyIncome.get()) +
                '}';
    }
}