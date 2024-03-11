package KlepetChat.Hilts

import KlepetChat.WebApi.Implementations.Repositories.AuthRepository
import KlepetChat.WebApi.Implementations.Repositories.ChatRepository
import KlepetChat.WebApi.Implementations.Repositories.HubRepository
import KlepetChat.WebApi.Implementations.Repositories.ImageRepository
import KlepetChat.WebApi.Implementations.Repositories.MessageRepository
import KlepetChat.WebApi.Implementations.Repositories.TokenRepository
import KlepetChat.WebApi.Implementations.Repositories.UserRepository
import KlepetChat.WebApi.Interfaces.IAuthService
import KlepetChat.WebApi.Interfaces.IChatService
import KlepetChat.WebApi.Interfaces.IHubService
import KlepetChat.WebApi.Interfaces.IImageService
import KlepetChat.WebApi.Interfaces.IMessageService
import KlepetChat.WebApi.Interfaces.ITokenService
import KlepetChat.WebApi.Interfaces.IUserService
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

    @Provides
    fun providesTokenRepository(tokenService: ITokenService) = TokenRepository(tokenService)

    @Provides
    fun providesChatRepository(chatService: IChatService) = ChatRepository(chatService)

    @Provides
    fun providesMessageRepository(messageService: IMessageService) =
        MessageRepository(messageService)

    @Provides
    fun providesImageRepository(imageService: IImageService) = ImageRepository(imageService)

    @Provides
    fun providesHubRepository(hubService: IHubService) = HubRepository(hubService)
}