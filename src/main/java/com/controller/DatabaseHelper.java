package com.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

// ABSTRACTION: This class demonstrates abstraction by:
// 1. Providing high-level methods for database operations that hide complex SQL details
// 2. Encapsulating database connection management and SQL execution logic
// 3. Simplifying database interactions for other parts of the application
public class DatabaseHelper {

    private static final String DB_PATH;
    private static final String DB_URL;
    
    // Public method to get DB URL for other classes
    public static String getDbUrl() {
        return DB_URL;
    }

    static {
        // Determine database path - use project directory
        String userDir = System.getProperty("user.dir");
        DB_PATH = Paths.get(userDir, "database", "loanshield.db").toAbsolutePath().toString();
        DB_URL = "jdbc:sqlite:" + DB_PATH;
        System.out.println("Database path: " + DB_PATH);
        
        try {
            // Ensure the SQLite driver is loaded and registered
            Class.forName("org.sqlite.JDBC");

            // Ensure parent directory exists to avoid path errors
            Path dbDir = Paths.get(userDir, "database");
            if (!Files.exists(dbDir)) {
                Files.createDirectories(dbDir);
            }

            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                String createApplications = """
                    CREATE TABLE IF NOT EXISTS applications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nic TEXT NOT NULL,
                        full_name TEXT,
                        phone TEXT,
                        email TEXT,
                        address TEXT,
                        employment_type TEXT,
                        job_title TEXT,
                        employment_duration_months INTEGER,
                        monthly_income REAL,
                        requested_loan_amount REAL,
                        loan_type TEXT,
                        loan_term_years INTEGER,
                        has_existing_loans INTEGER,
                        total_outstanding_loan REAL,
                        monthly_installments REAL,
                        credit_score INTEGER,
                        application_date TEXT,
                        application_status TEXT,
                        risk_score INTEGER,
                        rejection_reasons TEXT
                    );
                """;

                conn.createStatement().execute(createApplications);
                // Note: users table is managed by AuthRepository
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Legacy saveResult removed: results are unified under applications table

    // ABSTRACTION: This method provides a simple interface for saving loan applications
    // The complex SQL operations, connection management, and error handling are hidden from the caller
    public static boolean saveApplication(com.loanshield.LoanApplicationData data,
                                          int riskScore,
                                          java.util.List<String> rejectionReasons) {
        String insertSQL = """
            INSERT INTO applications (
                nic, full_name, phone, email, address, employment_type, job_title,
                employment_duration_months, monthly_income, requested_loan_amount, loan_type,
                loan_term_years, has_existing_loans, total_outstanding_loan, monthly_installments,
                credit_score, application_date, application_status, risk_score, rejection_reasons
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        System.out.println("=== Saving Application to Database ===");
        System.out.println("NIC: " + data.getNic());
        System.out.println("Name: " + data.getFullName());
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, data.getNic());
            pstmt.setString(2, data.getFullName());
            pstmt.setString(3, data.getPhone());
            pstmt.setString(4, data.getEmail());
            pstmt.setString(5, data.getAddress());
            pstmt.setString(6, data.getEmploymentType());
            pstmt.setString(7, data.getJobTitle());
            pstmt.setInt(8, data.getEmploymentDuration());
            pstmt.setDouble(9, data.getMonthlyIncome());
            pstmt.setDouble(10, data.getRequestedLoanAmount());
            pstmt.setString(11, data.getLoanType());
            pstmt.setInt(12, data.getLoanTermYears());
            pstmt.setInt(13, data.hasExistingLoans() ? 1 : 0);
            pstmt.setDouble(14, data.getTotalOutstandingLoan());
            pstmt.setDouble(15, data.getMonthlyInstallments());
            pstmt.setInt(16, data.getCreditScore());
            pstmt.setString(17, data.getApplicationDate());
            pstmt.setString(18, data.getApplicationStatus());
            pstmt.setInt(19, riskScore);
            pstmt.setString(20, rejectionReasons == null ? null : String.join("; ", rejectionReasons));

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("=== Application Saved Successfully ===");
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("=== ERROR: Failed to save application ===");
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ABSTRACTION: This method provides a simple interface for updating loan applications
    // Complex SQL update logic and connection management are abstracted away
    public static boolean updateLatestApplication(String nic,
                                                  String newStatus,
                                                  Integer riskScore,
                                                  String rejectionReasons) {
        String updateSql = """
            UPDATE applications
               SET application_status = ?,
                   risk_score = COALESCE(?, risk_score),
                   rejection_reasons = COALESCE(?, rejection_reasons)
             WHERE id = (
                 SELECT id FROM applications WHERE nic = ? ORDER BY id DESC LIMIT 1
             )
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setString(1, newStatus);
            if (riskScore == null) {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(2, riskScore);
            }
            if (rejectionReasons == null) {
                pstmt.setNull(3, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(3, rejectionReasons);
            }
            pstmt.setString(4, nic);
            int updated = pstmt.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


