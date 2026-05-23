package com.nextstep.backend.services;

import com.nextstep.backend.dtos.AuthDTO;
import com.nextstep.backend.dtos.TokenDTO;

public interface AuthService {
    String registrar(AuthDTO data);
    TokenDTO login(AuthDTO data);
}