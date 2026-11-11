package mk.mdt.budgetapi.email;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}