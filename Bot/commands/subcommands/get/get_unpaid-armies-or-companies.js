const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");
const {createUnpaidStringArray} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        axios.get(`http://${serverIP}:${serverPort}/api/army/unpaid`)
            .then(async function (response) {

                let armyArray;
                if (response.data == undefined) {
                    armyArray = [];
                } else {
                    armyArray = response.data
                }

                console.log(response.data)
                unpaidStringArray = createUnpaidStringArray(armyArray);

                var replyEmbed = new MessageEmbed()
                    .setTitle("Unpaid Armies or Companies")
                    .setDescription("Armies or Companies that have not been payed for:")
                    .setFields([
                        {name: "Name", value:unpaidStringArray[0], inline: true},
                        {name: "Faction", value:unpaidStringArray[1], inline: true},
                        {name: "Created at", value:unpaidStringArray[2], inline: true}
                    ])
                    .setColor("GREEN")
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
            .catch(async function(error) {
                let errorMessage;
                if(error.response == undefined) {
                    errorMessage = error.toString()
                }
                else {
                    errorMessage = error.response.data.message
                }

                console.log(error)

                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to get unpaid data")
                    .setColor("RED")
                    .setDescription(errorMessage)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })

    }
}
