package commands.utility

import commandhandler.CommandContext
import commands.BaseCommand
import commands.CommandType.Utility
import database.colourmeRoles
import ext.required
import ext.useArguments
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.requests.ErrorResponse
import java.awt.Color

class Colourme : BaseCommand(
    commandName = "colourme",
    commandDescription = "Create a custom role for yourself",
    commandType = Utility,
    commandArguments = mapOf("color".required(), "role name".required()),
    commandAliases = listOf("colorme", "color", "colour")
) {

    override fun execute(ctx: CommandContext) {
        super.execute(ctx)
        val guildId = ctx.guild.id
        val args = ctx.args
        if (args.isNotEmpty() && args.size >= 2) {
            val color = try {
                Color.decode("#${args[0].removePrefix("#")}")
            } catch (e: Exception) {
                Color.CYAN
            }

            ctx.guild.retrieveMemberById(ctx.author.id).queue member@ { member ->
                if (member.roles.none { guildId.colourmeRoles.contains(it.id) }) {
                    ctx.event.channel.sendMsg("You are not allowed to use this command!")
                    return@member
                }
                val roleName = args.apply { removeAt(0) }.joinToString(" ")

                if (roleName.length > 100) {
                    ctx.event.channel.sendMsg("Role name can't be more than 100 characters!")
                    return@member
                }

                val ccrole = member.roles.filter { it.name.endsWith("CC") }
                fun addRole() {
                    ctx.guild.createRole().setColor(color).setName("$roleName-CC").queue({ role ->
                        ctx.guild.modifyRolePositions().selectPosition(role).moveTo(member.roles.first().position + 1).queue {
                            ctx.guild.addRoleToMember(member, role).queue {
                                ctx.event.channel.sendMsg("Successfully added the role!")
                            }
                        }
                    }, ErrorHandler().handle(ErrorResponse.MAX_ROLES_PER_GUILD) {
                        ctx.event.channel.sendMsg("Guild reached maximum amount of roles!")
                    })
                }

                if (ccrole.isNotEmpty()) {
                    ccrole[0].delete().queue {
                        addRole()
                    }
                } else {
                    addRole()
                }

            }
        } else {
            ctx.channel.useArguments(2)
        }
    }

}