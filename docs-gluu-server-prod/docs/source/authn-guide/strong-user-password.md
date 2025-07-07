# Enabling Strong Password Enforcement in Gluu 4

To ensure better security and compliance with password policies, **Gluu Server 4** offers built-in support for strong password enforcement. This feature allows administrators to define custom rules that users must follow when setting or changing their passwords.

By enabling strong password validation, you can enforce complexity requirements such as:

- Use of **uppercase** and **lowercase** letters  
- Inclusion of **numbers**  
- Requirement for **special characters**  
- Enforcement of **minimum and maximum password lengths**

These rules help prevent weak passwords that could be easily guessed or compromised through brute-force attacks.

This guide walks you through the steps to enable and configure strong password policies using regular expressions and length settings within the **oxTrust** administrative interface.

---

## Steps to Enable Strong Password Policy

1. **Log into oxTrust**
2. Navigate to:  
   **Configuration** → **Attributes**
3. Locate the `userPassword` attribute and enable:  
   **"Enable custom validation for this attribute"**
4. In the **"Validation RegExp"** field, enter your desired regular expression.  
   Example (for strong password requirements):
   ```regex title='Regular Expression'
   ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$
   ```
