package org.kaazing.gateway.transport.wsn.auth;

import static org.junit.rules.RuleChain.outerRule;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.gateway.server.test.GatewayRule;
import org.kaazing.gateway.server.test.config.GatewayConfiguration;
import org.kaazing.gateway.server.test.config.builder.GatewayConfigurationBuilder;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;
import org.kaazing.test.util.MethodExecutionTrace;

public class AuthIT {
    
    private TestRule trace = new MethodExecutionTrace();
    
    private TestRule timeout = new DisableOnDebug(new Timeout(7, TimeUnit.SECONDS));

    private final K3poRule robot = new K3poRule();

    public GatewayRule gateway = new GatewayRule() {
        {

            GatewayConfiguration configuration = new GatewayConfigurationBuilder()
                    .service()
                        .accept(URI.create("ws://localhost:8000/echo")) // normal configuration
                        .type("echo")
                        .realmName("demo")
                        .authorization()
                            .requireRole("AUTHORIZED")
                         .done()
                        .crossOrigin()
                            .allowOrigin("*")
                        .done()
                    .done()
                    .service() 
                        .accept(URI.create("ws://localhost:8001/echo")) // No authorization constraints
                        .type("echo")
                        .crossOrigin()
                            .allowOrigin("*")
                        .done()
                    .done()
                    .service()
                        .accept(URI.create("ws://localhost:8002/echo")) // Any Authorized user
                        .type("echo")
                        .realmName("demo")
                        .authorization()
                            .requireRole("*")
                         .done()
                        .crossOrigin()
                            .allowOrigin("*")
                        .done()
                    .done()
                    .service()
                        .accept(URI.create("ws://localhost:8003/echo")) // No authorization
                        .type("echo")
                        .realmName("demo")
                        .crossOrigin()
                            .allowOrigin("*")
                        .done()
                    .done()
                    .security()
                        .realm()
                              .name("demo")
                              .description("Kaazing WebSocket Gateway Demo")
                              .httpChallengeScheme("Basic")
                              .authorizationMode("challenge")
                              .loginModule()
                                   .type("file")
                                 .success("required")
                                 .option("file", "src/test/resources/gateway/conf/jaas-config.xml")
                              .done()
                        .done()
                    .done()
                    .done();
            init(configuration); // , "log4j-diagnostic.properties");  // for debugging
        }
    };

    @Rule
    public TestRule chain = outerRule(trace).around(robot).around(gateway).around(timeout);
    
    @Specification("authorized.credentials.reponds.101")
    @Test
    public void authorizedCredentialsReponds101() throws Exception  {
        robot.finish();
    }

    @Specification("unauthorized.credentials.responds.401")
    @Test
    public void unauthorizedCredentialsResponds401() throws Exception  {
        robot.finish();
    }

    @Specification("should.pass.with.no.realm.correct.user.login")
    @Test
    public void shoudlPassWithNoRealmCorrectUserLogin() throws Exception  {
        robot.finish();
    }

    @Specification("should.pass.with.no.realm.incorrect.user.login")
    @Test
    public void shouldPassWithNoRealmIncorrectUserLogin() throws Exception  {
        robot.finish();
    }
    
    @Specification("should.pass.with.admin.or.authorized.users")
    @Test
    public void shouldPassWithAdminOrAuthorizedUsers() throws Exception  {
        robot.finish();
    }

    @Specification("should.respond.401.to.unauth.user.when.accepting.any.auth.user")
    @Test
    public void shouldRespond401ToUnauthUserWhenAcceptingAnyAuthUser() throws Exception  {
        robot.finish();
    }

    @Specification("should.pass.with.realm.no.authorization.correct.user.login")
    @Test
    public void shouldPassWithRealmNoAuthorizationCorrectUserLogin() throws Exception  {
        robot.finish();
    }

    @Specification("should.pass.with.realm.no.authorization.incorrect.user.login")
    @Test
    public void shouldPassWithRealmNoAuthorizationIncorrectUserLogin() throws Exception  {
        robot.finish();
    }
    
    @Specification("multiple.requests.with.unauth.and.auth.users")
    @Test
    public void multipleRequestsWithUnauthAndAuthUsers() throws Exception  {
        robot.finish();
    }

}

