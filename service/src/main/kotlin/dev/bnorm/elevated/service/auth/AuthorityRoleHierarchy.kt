package dev.bnorm.elevated.service.auth

import dev.bnorm.elevated.model.auth.Authority
import dev.bnorm.elevated.model.auth.Role
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class AuthorityRoleHierarchy : RoleHierarchy {

    override fun getReachableGrantedAuthorities(authorities: Collection<GrantedAuthority>): Collection<GrantedAuthority> {
        return authorities.asSequence()
            .mapNotNull { authority -> authority.toRole() }
            .flatMap { it.authorities }
            .map { it.toGrantedAuthority() }
            .toSet()
    }

    private fun Authority.toGrantedAuthority(): GrantedAuthority = SimpleGrantedAuthority(name)

    private fun GrantedAuthority.toRole(): Role? =
        Role.values().find { role -> role.name == authority }

}
