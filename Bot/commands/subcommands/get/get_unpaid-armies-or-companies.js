const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");
const {createUnpaidString} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        axios.get(`http://${serverIP}:${serverPort}/api/army/unpaid`)
            .then(async function (response) {

                console.log(response.data)
                unpaidString = createUnpaidString(response.data);

                var replyEmbed = new MessageEmbed()
                    .setTitle("Unpaid Armies or Companies")
                    .setDescription("Armies or Companies that have not been payed for!")
                    .setFields([
                        {name: "Armies and Companies", value:unpaidString, inline: false}
                    ])
                    .setColor("GREEN")
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to get unpaid data")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })

    }
}
