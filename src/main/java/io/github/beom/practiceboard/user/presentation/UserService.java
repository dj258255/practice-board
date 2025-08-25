package io.github.beom.practiceboard.user.presentation;

import io.github.beom.practiceboard.user.domain.User;
import io.github.beom.practiceboard.user.exception.IdExistException;
import io.github.beom.practiceboard.user.presentation.dto.request.UserRegisterRequestDTO;

public interface UserService {
    //사용자 회원가입
    void join(UserRegisterRequestDTO userRegisterRequestDTO) throws IdExistException;
    //사용자 ID로 조회
    User findById(String id);
    //사용자 이메일로 조회
    User findByEmail(String email);
    //사용자 정보 수정
    void updateUser(String id, String email);
    //사용자 논리적 삭제
    void deleteUser(String id);

    // 예외 클래스
    class IdExistException extends Exception {
        public IdExistException(String message) {
            super(message);
        }
    }
}
