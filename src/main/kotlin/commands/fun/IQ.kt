package commands.`fun`

import commandhandler.CommandContext
import commands.BaseCommand
import commands.CommandType.Fun
import ext.optional
import net.dv8tion.jda.api.entities.TextChannel
import org.apache.commons.math3.distribution.NormalDistribution

class IQ : BaseCommand(
    commandName = "iq",
    commandDescription = "Calculate IQ level",
    commandType = Fun,
    commandArguments = mapOf("The thing".optional()),
    commandAliases = listOf("howsmart")
) {
    override fun execute(ctx: CommandContext) {
        super.execute(ctx)
        val args = ctx.args
        if (args.isNotEmpty()) {
            ctx.channel.calculateIQ(args.joinToString(" "))
        } else {
            val user = ctx.author.asMention
            ctx.channel.calculateIQ(user)
        }

    }

    private fun TextChannel.calculateIQ(arg: String) {
        sendMsg(
            embedBuilder.apply {
                setTitle("IQ Calculator")
                setDescription("$arg has an iq of ${calcIQ()}")
            }.build()
        )
    }

    private fun calcIQ(): Int {
        val iqDist = NormalDistribution(100.0, 15.0)
        val randIQ = (50..150).random()
        val luck = Math.random()
        val smallo = iqDist.cumulativeProbability(randIQ.toDouble()) < 0.5
        return if (smallo && iqDist.cumulativeProbability(randIQ.toDouble()) > luck || !smallo && iqDist.cumulativeProbability(randIQ.toDouble()) < luck) {
            randIQ
        } else {
            calcIQ()
        }
    }

}