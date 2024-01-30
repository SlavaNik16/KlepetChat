package KlepetChat.WebApi.Hilt

import KlepetChat.WebApi.Implementations.Repositories.AuthRepository
import KlepetChat.WebApi.Interfaces.IAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {

    @Provides
    fun provideAuthRepository(authApiService: IAuthService) = AuthRepository(authApiService)
}