package dev.bnorm.elevated.service.auth

import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.core.GrantedAuthority

class AuthorityRoleHierarchy : RoleHierarchy {

    override fun getReachableGrantedAuthorities(authorities: Collection<GrantedAuthority>): Collection<GrantedAuthority> {
        return authorities.asSequence()
            .mapNotNull { authority -> authority.toRole() }
            .flatMap { it.authorities }
            .map { it.toGrantedAuthority() }
            .toSet()
    }
}
