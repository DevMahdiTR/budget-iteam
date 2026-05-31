package com.iteam.buget.core.auth;

import com.iteam.buget.core.auth.dto.LoginRequest;
import com.iteam.buget.core.auth.dto.LoginResponse;
import com.iteam.buget.core.auth.dto.RegisterRequest;

public interface AuthenticationService {

    String register(final RegisterRequest request);

    LoginResponse login(final LoginRequest request);

}
