package com.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

// Encapsulates search-related logic for loan applications


public class SearchController {

    @FXML private TextField searchField;
    @FXML private TableView<ApplicationRow> resultsTable;
    @FXML private TableColumn<ApplicationRow, Integer> colId;
    @FXML private TableColumn<ApplicationRow, String> colNic;
    @FXML private TableColumn<ApplicationRow, String> colFullName;
    @FXML private TableColumn<ApplicationRow, Double> colMonthlyIncome;
    @FXML private TableColumn<ApplicationRow, Double> colLoanAmount;
    @FXML private TableColumn<ApplicationRow, Integer> colLoanTerm;
    @FXML private TableColumn<ApplicationRow, String> colStatus;
    @FXML private TableColumn<ApplicationRow, Integer> colRisk;

    // Use the same DB URL as DatabaseHelper for consistency
    private static String getDbUrl() {
        return DatabaseHelper.getDbUrl();
    }

    // METHOD: initialize() — Abstraction (UI setup hidden from outside calls)
    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNic.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colMonthlyIncome.setCellValueFactory(new PropertyValueFactory<>("monthlyIncome"));
        colLoanAmount.setCellValueFactory(new PropertyValueFactory<>("requestedLoanAmount"));
        colLoanTerm.setCellValueFactory(new PropertyValueFactory<>("loanTermYears"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("applicationStatus"));
        colRisk.setCellValueFactory(new PropertyValueFactory<>("riskScore"));
        
        // Add row click listener to navigate to certificate
        resultsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                ApplicationRow selectedRow = resultsTable.getSelectionModel().getSelectedItem();
                if (selectedRow != null) {
                    openCertificate(selectedRow.getNic());
                }
            }
        });
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String q = searchField.getText() == null ? "" : searchField.getText().trim();
        
        System.out.println("=== Search Started ===");
        System.out.println("Search query: " + q);
        System.out.println("Database URL: " + getDbUrl());
        
        ObservableList<ApplicationRow> rows = FXCollections.observableArrayList();
        
        // Search for applications - if empty show all, otherwise filter by NIC
        String sql = q.isEmpty() ?
                "SELECT id, nic, full_name, phone, email, monthly_income, requested_loan_amount, " +
                "loan_term_years, application_status, risk_score FROM applications ORDER BY id DESC" :
                "SELECT id, nic, full_name, phone, email, monthly_income, requested_loan_amount, " +
                "loan_term_years, application_status, risk_score FROM applications WHERE nic LIKE ? ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!q.isEmpty()) {
                ps.setString(1, "%" + q + "%");
            }
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                rows.add(new ApplicationRow(
                        rs.getInt("id"),
                        rs.getString("nic"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDouble("monthly_income"),
                        rs.getDouble("requested_loan_amount"),
                        rs.getInt("loan_term_years"),
                        rs.getString("application_status"),
                        rs.getInt("risk_score")
                ));
            }
            System.out.println("Found " + count + " applications");
            resultsTable.setItems(rows);
            
            if (rows.isEmpty()) {
                showAlert("No applications found" + (q.isEmpty() ? "." : " for NIC: " + q));
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error searching for applications: " + e.getMessage());
        }
    }
    
    // Opens the certificate view for the selected NIC
    private void openCertificate(String nic) {
        String sql = "SELECT nic, full_name, application_date, requested_loan_amount, " +
                     "loan_term_years, application_status, risk_score, rejection_reasons " +
                     "FROM applications WHERE nic = ? ORDER BY id DESC LIMIT 1";
        
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nic);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String fullName = rs.getString("full_name");
                String applicationDate = rs.getString("application_date");
                // Use current date if database date is null or empty
                if (applicationDate == null || applicationDate.isEmpty()) {
                    applicationDate = LocalDate.now().toString();
                }
                double loanAmount = rs.getDouble("requested_loan_amount");
                int riskScore = rs.getInt("risk_score");
                int loanTerm = rs.getInt("loan_term_years");
                String status = rs.getString("application_status");
                String rejectionReasonsStr = rs.getString("rejection_reasons");
                
                Stage stage = (Stage) resultsTable.getScene().getWindow();
                
                // Check if application is approved or rejected
                if ("APPROVED".equalsIgnoreCase(status)) {
                    // Load approved certificate
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/appro.fxml"));
                    Parent root = loader.load();
                    
                    resultController controller = loader.getController();
                    controller.setApplicationData(fullName, nic, applicationDate, loanAmount, riskScore, loanTerm);
                    controller.setNicNumber(nic);
                    
                    stage.setScene(new Scene(root));
                    stage.show();
                } else if ("REJECTED".equalsIgnoreCase(status)) {
                    // Load rejected certificate
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/reject.fxml"));
                    Parent root = loader.load();
                    
                    rejectController controller = loader.getController();
                    
                    // Parse rejection reasons
                    List<String> rejectionReasons = new ArrayList<>();
                    if (rejectionReasonsStr != null && !rejectionReasonsStr.isEmpty()) {
                        rejectionReasons = Arrays.asList(rejectionReasonsStr.split("; "));
                    }
                    
                    controller.setApplicationData(fullName, nic, applicationDate, loanAmount, riskScore, rejectionReasons);
                    controller.setNicNumber(nic);
                    
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    showAlert("Application status: " + status + "\nCertificate not available yet. Please save the application first.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading application: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading certificate: " + e.getMessage());
        }
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search Result");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // METHOD: Handles navigation back — Abstraction of UI navigation logic
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Step1.fxml"));
            Parent step1Root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(step1Root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//encapsulation:..........

    
    public static class ApplicationRow {
        private final int id;
        private final String nic;
        private final String fullName;
        private final String phone;
        private final String email;
        private final double monthlyIncome;
        private final double requestedLoanAmount;
        private final int loanTermYears;
        private final String applicationStatus;
        private final int riskScore;


        // Constructor for ApplicationRow
//  CONSTRUCTOR: Initializes object state (Encapsulation)
        public ApplicationRow(int id, String nic, String fullName, String phone, String email,
                               double monthlyIncome, double requestedLoanAmount, int loanTermYears,
                               String applicationStatus, int riskScore) {
            this.id = id;
            this.nic = nic;
            this.fullName = fullName;
            this.phone = phone;
            this.email = email;
            this.monthlyIncome = monthlyIncome;
            this.requestedLoanAmount = requestedLoanAmount;
            this.loanTermYears = loanTermYears;
            this.applicationStatus = applicationStatus;
            this.riskScore = riskScore;
        }

//  GETTERS: Provide controlled access to fields (Encapsulation)
        // Getters for ApplicationRow fields
        public int getId() { return id; }
        public String getNic() { return nic; }
        public String getFullName() { return fullName; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public double getMonthlyIncome() { return monthlyIncome; }
        public double getRequestedLoanAmount() { return requestedLoanAmount; }
        public int getLoanTermYears() { return loanTermYears; }
        public String getApplicationStatus() { return applicationStatus; }
        public int getRiskScore() { return riskScore; }
    }
}


