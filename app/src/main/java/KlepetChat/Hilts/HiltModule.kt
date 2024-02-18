package KlepetChat.Hilts

import KlepetChat.WebApi.Implementations.Repositories.AuthRepository
import KlepetChat.WebApi.Implementations.Repositories.UserRepository
import KlepetChat.WebApi.Interfaces.IAuthService
import KlepetChat.WebApi.Interfaces.User.IUserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {
    @Provides
    fun providesAuthRepository(authApiService: IAuthService) = AuthRepository(authApiService)

    @Provides
    fun providesUserRepository(userService: IUserService) = UserRepository(userService)
}