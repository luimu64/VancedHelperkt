package commands.utility

import commandhandler.CommandContext
import commands.BaseCommand
import commands.CommandType.Utility
import ext.required
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.requests.ErrorResponse

class Avatar : BaseCommand(
    commandName = "avatar",
    commandDescription = "Get a user avatar",
    commandType = Utility,
    commandArguments = mapOf("User Mention | User ID".required()),
    commandAliases = listOf("av", "pfp", "icon")
) {

    override fun execute(ctx: CommandContext) {
        super.execute(ctx)
        val args = ctx.args
        if (args.isNotEmpty()) {
            sendAvatar(args[0].filter { it.isDigit() }, ctx)
        } else {
            sendAvatar(ctx.author.id, ctx)
        }

    }

    private fun sendAvatar(userId: String, ctx: CommandContext) {
        if (userId.isEmpty()) {
            ctx.event.channel.sendMsg("Provided user does not exist!")
            return
        }
        ctx.guild.retrieveMemberById(userId).queue({
            val userUrl = it.user.avatarUrl
            if (userUrl == null) {
                ctx.event.channel.sendMsg("Could not get Avatar URL for you")
                return@queue
            }
            ctx.event.channel.sendMsg(
                embedBuilder.apply {
                    val url = "$userUrl?size=256"
                    setTitle("${it.user.name}'s avatar")
                    setImage(url)
                    setDescription("[Avatar URL]($url)")
                }.build()
            )
        }, ErrorHandler().handle(ErrorResponse.UNKNOWN_USER) {
            ctx.event.channel.sendMsg("Provided user does not exist!")
        })

    }

}