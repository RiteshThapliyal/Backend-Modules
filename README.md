Logout & Token Revocation — Security vs. Complexity Trade-off

I am making several trade-offs in this project based on business requirements. One of the important trade-offs is between security and system complexity.

When implementing the logout endpoint, revoking the refresh token is important because it prevents the client from generating new access tokens after logout.

However, the access token has a short lifetime of 15 minutes. If I also want to invalidate the access token immediately during logout, I would need additional server-side state or a token blacklist/revocation mechanism. This would add complexity and introduce additional storage and lookup operations.

For this project, I have decided to make the following trade-off:

Revoke the refresh token immediately during logout.
Allow the existing access token to remain valid until it expires.
The access token has a short lifetime of 15 minutes, which limits the window of exposure after logout.

This means that after logout, an already-issued access token may technically remain usable for its remaining lifetime. However, the user cannot use the revoked refresh token to obtain a new access token.

This is a deliberate decision to balance security, performance, and implementation complexity based on the current business requirements.

Security decision: Refresh token is revoked on logout; access token is allowed to expire naturally.
