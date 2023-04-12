package io.critica.di

//fun Application.configureSecurity(jwtConfig: JwtConfig, userTokenDao: UserTokenDao) {
//
//    install(Authentication) {
//        jwt {
//            realm = jwtConfig.realm
//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256(jwtConfig.secret))
//                    .withAudience(jwtConfig.audience)
//                    .withIssuer(jwtConfig.issuer)
//                    .acceptExpiresAt(jwtConfig.expiration)
//                    .build()
//            )
//            validate {
//                val userId = it.payload.getClaim("sub").asInt()
//                val userToken = userTokenDao.findByUserId(userId)
//
//                if (userToken != null) {
//                    JWTPrincipal(it.payload)
//                } else {
//                    null
//                }
//            }
//        }
//    }
//}

