package com.project.petcareapp.service;

import com.project.petcareapp.model.EmailVerified;

import java.util.List;

public interface EmailVerifiedService {
   List<EmailVerified> getEmailVerifed(int accountId);

   boolean verifyEmail(EmailVerified emailVerified, int accountId);



}
