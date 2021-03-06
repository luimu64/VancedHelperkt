package commands.`fun`

import commandhandler.CommandContext
import commands.BaseCommand
import commands.CommandType.Fun
import ext.optional
import net.dv8tion.jda.api.entities.TextChannel
import org.apache.commons.math3.distribution.NormalDistribution

class PPSize : BaseCommand(
    commandName = "ppsize",
    commandDescription = "Calculate PP size",
    commandType = Fun,
    commandAliases = listOf("pp", "coce"),
    commandArguments = mapOf("The thing".optional())
) {

    override fun execute(ctx: CommandContext) {
        super.execute(ctx)
        val args = ctx.args
        val event = ctx.event
        val channel = ctx.channel
        if (args.isNotEmpty()) {
            channel.calculatePPSize(args.joinToString(" "))
        } else {
            channel.calculatePPSize(event.author.asMention)
        }

    }

    private fun TextChannel.calculatePPSize(thing: String) {
        val ppsize = calcPP()
        val bar = "8" + "=".repeat(ppsize) + "D"
        sendMsg(
            embedBuilder.apply {
                setTitle("PP Size Calculator")
                setDescription("$thing has a PP size of $ppsize inches\n$bar")
            }.build()
        )
    }

    private fun calcPP(): Int {
        val ppDist = NormalDistribution(6.0, 1.0)
        val randPP = (2..14).random()
        val luck = Math.random()
        val smallo = ppDist.cumulativeProbability(randPP.toDouble()) < 0.5
        return if (smallo && ppDist.cumulativeProbability(randPP.toDouble()) > luck || !smallo && ppDist.cumulativeProbability(randPP.toDouble()) < luck) {
            randPP
        } else {
            calcPP()
        }
    }

}