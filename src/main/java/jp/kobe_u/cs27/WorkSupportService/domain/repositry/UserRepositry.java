package jp.kobe_u.cs27.WorkSupportService.domain.repositry;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.User;

/**
 * ユーザのリポジトリ
 *
 * @author ing
 */
@Repository
public interface UserRepositry extends CrudRepository<User, String> {
}
