package com.project.petcareapp.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressResult;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.EmailVerified;
import com.project.petcareapp.repository.AccountRepository;
import com.project.petcareapp.repository.EmailVerifiedRepository;
import com.project.petcareapp.service.EmailVerifiedService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailVerifiedServiceImpl implements EmailVerifiedService {

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    EmailVerifiedRepository emailVerifiedRepository;

    @Override
    public List<EmailVerified> getEmailVerifed(int accountId) {
        List<String> emailVerify = emailVerifiedRepository.findEmailVerifiedBy(accountId);
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(awsRegion).withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))).build();
        List<String> emailVerifiedOnSES = client.listVerifiedEmailAddresses().getVerifiedEmailAddresses();

        List<String> emailVerified = new ArrayList<>(CollectionUtils.intersection(emailVerify, emailVerifiedOnSES));

List<EmailVerified> emailVerifyByAccountId = emailVerifiedRepository.findDistinct(accountId);


for (EmailVerified verified : emailVerifyByAccountId) {
            for (int counter = 0; counter < emailVerified.size(); counter++) {
                if (verified.getEmail().equals(emailVerified.get(counter))) {
                    verified.setVerified(true);
                    emailVerifiedRepository.save(verified);
                }

                emailVerifiedRepository.save(verified);

            }

        }


        return emailVerifyByAccountId;
    }

    @Override
    public boolean verifyEmail(EmailVerified emailVerified, int accountId) {

        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(awsRegion).withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))).build();
        VerifyEmailAddressResult res = client.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(emailVerified.getEmail()));
        EmailVerified emailVerified1 = new EmailVerified();

        EmailVerified temp = emailVerifiedRepository.findEmailVerifiedByEmailAndAccount_id(emailVerified.getEmail(), accountId);
        if (temp != null) {
            if (temp.isVerified()) {
                return false;
            }
            client.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(temp.getEmail()));
            return true;
        }
        emailVerified1.setEmail(emailVerified.getEmail());
        Account account = accountRepository.findAccountById(accountId);
        emailVerified1.setAccount_id(account.getId());
        emailVerifiedRepository.save(emailVerified1);
        return true;
    }


}
