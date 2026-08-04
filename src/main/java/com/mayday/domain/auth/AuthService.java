package com.mayday.domain.auth;

import com.mayday.domain.auth.dto.AuthResponse;
import com.mayday.domain.auth.dto.LoginRequest;
import com.mayday.domain.auth.dto.SignUpRequest;
import com.mayday.domain.user.User;
import com.mayday.domain.user.UserRepository;
import com.mayday.global.exception.InvalidLoginException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mayday.global.security.JwtTokenProvider;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //비밀번호, 비밀번호 확인 같은지 검사
    private void validatePasswordMatch(String password, String passwordConfirm){
        if(!password.equals(passwordConfirm)){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    //영문+숫자 검사
    private void validatePassword(String password){
        if(password.length()<8){
            throw new IllegalArgumentException("비밀번호는 8자리 이상이어야 합니다.");
        }

        if(!password.matches(".*[a-zA-Z].*")){
            throw new IllegalArgumentException("비밀번호는 영문이 포함되어야 합니다.");
        }

        if(!password.matches(".*\\d.*")){
            throw new IllegalArgumentException("비밀번호는 숫자가 포함되어야 합니다.");
        }
    }

    //필수 동의값이 모두 true인지 검사
    private void validateAgreement(boolean agreedToTerms, boolean agreedToPrivacy,
                                   boolean agreedToReceiptAnalysis){
        if(!agreedToTerms || !agreedToPrivacy
                || !agreedToReceiptAnalysis){
            throw new IllegalArgumentException("필수 약관에 모두 동의해주세요.");
        }
    }

    //이메일 중복 검사
    private void validateDuplicateEmail(String email){
        if (userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    //회원가입
    public void signup(SignUpRequest request){
        validateDuplicateEmail(request.getEmail());

        validatePasswordMatch(
                request.getPassword(),
                request.getPasswordConfirm()
        );

        validatePassword(
                request.getPassword()
        );

        validateAgreement(request.isAgreedToTerms(),
                request.isAgreedToPrivacy(), request.isAgreedToReceiptAnalysis());


        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.isAgreedToTerms(),
                request.isAgreedToPrivacy(),
                request.isAgreedToReceiptAnalysis()
        );

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidLoginException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidLoginException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail());

        return new AuthResponse(accessToken, "Bearer", user.getId(), user.getEmail(), user.isOnboardingCompleted());
    }
}
