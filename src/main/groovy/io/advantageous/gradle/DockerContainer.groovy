package io.advantageous.gradle

import javax.annotation.Nullable

 interface DockerContainer {

    @Nullable
    String getUsername()

    void setUsername(@Nullable String userName);

    @Nullable
    String getPassword();

    void setPassword(@Nullable String password);
}
