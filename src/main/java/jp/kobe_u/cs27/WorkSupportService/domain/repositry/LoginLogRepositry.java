package jp.kobe_u.cs27.WorkSupportService.domain.repositry;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.LoginLog;

/**
 * ログインログのリポジトリ
 *
 * @author ing
 */
@Repository
public interface LoginLogRepositry extends CrudRepository<LoginLog, String> {
}
