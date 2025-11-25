# Authentication & User Identity API Endpoints

The following endpoints are available for user authentication and identity management.

| Method | Endpoint                          | Description                                                                                              |
|--------|-----------------------------------|----------------------------------------------------------------------------------------------------------|
| POST   | `/api/v1/auth/register`           | User registration with email and password. The user role is auto-assigned as USER, and a verification email is sent. |
| POST   | `/api/v1/auth/login`              | User login with email and password. Returns a JWT access token and refresh token on successful authentication. |
| GET    | `/api/v1/auth/verify?token={token}`| Verify user email using the verification token sent to the provided email address.                        |
| POST   | `/api/v1/auth/forgot-password`    | Request password reset email. Requires email verification before requesting a password reset.            |
| POST   | `/api/v1/auth/reset-password`     | Reset user password using the token received in the password reset email. Passwords must match and follow security requirements. |

## Endpoint Details

### 1. **User Registration** - `/api/v1/auth/register`
- **Method**: `POST`
- **Description**: Registers a new user with email and password. A verification email will be sent to the provided email address. The role is automatically assigned as `USER`.

#### Responses:
- `201`: User registered successfully, verification email sent.
- `400`: Invalid input or email already exists.
- `500`: Internal server error.

---

### 2. **User Login** - `/api/v1/auth/login`
- **Method**: `POST`
- **Description**: Logs the user in using email and password. Returns a JWT token upon successful authentication.

#### Responses:
- `200`: Login successful, JWT token returned.
- `400`: Invalid email or password.
- `401`: Authentication failed.
- `500`: Internal server error.

---

### 3. **Verify User Email** - `/api/v1/auth/verify?token={token}`
- **Method**: `GET`
- **Description**: Verifies the user’s email with the token sent during registration. Both email and token are required.

#### Responses:
- `200`: Email verified successfully.
- `400`: Invalid or expired verification token.
- `404`: User not found.
- `500`: Internal server error.

---

### 4. **Request Password Reset** - `/api/v1/auth/forgot-password`
- **Method**: `POST`
- **Description**: Sends a password reset link to the user’s email. The user must verify their email before requesting a password reset.

#### Responses:
- `200`: Password reset email sent successfully.
- `400`: Invalid email or email not verified.
- `404`: User not found.
- `500`: Internal server error or email sending failure.

---

### 5. **Reset Password** - `/api/v1/auth/reset-password`
- **Method**: `POST`
- **Description**: Resets the user’s password using the reset token sent in the password reset email. The new password must match the confirmation password and meet security requirements.

#### Responses:
- `200`: Password reset successfully.
- `400`: Invalid token, expired token, or password mismatch.
- `404`: User not found.
- `500`: Internal server error.

---
