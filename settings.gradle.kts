plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "vtbet"
include("vtbet-main")
include("vtbet-user-accounter")
include("vtbet-sports")
include("vbet-bets-handler")
include("vtbet-common")
include("vtbet-eureka")
include("vtbet-configs")
include("vtbet-auth")
include("vtbet-s3")
include("gateway-server")
include("vtbet-analytics-server")
