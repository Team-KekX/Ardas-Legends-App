const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");
const {createUnpaidString} = require("../../../utils/utilities");

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

                console.log(armyArray)
                console.log(`Data ${response.data}`)
                unpaidString = createUnpaidString(armyArray);

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
                let errorMessage;
                if(error.response == undefined) {
                    errorMessage = error.toString()
                }
                else {
                    errorMessage = error.response.data.message
                }

                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to get unpaid data")
                    .setColor("RED")
                    .setDescription(errorMessage)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })

    }
}
