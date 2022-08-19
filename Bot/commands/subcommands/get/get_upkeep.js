const {MessageEmbed} = require('discord.js');
const {UNBIND} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {

        axios.get(`http://${serverIP}:${serverPort}/api/army/upkeep`)
            .then(async function (response) {
                console.log(response.data);
                var upkeepArray = response.data;
                var arrayLength = upkeepArray.length
                var replyEmbed = new MessageEmbed()
                    .setTitle("Upkeep of all Factions")
                    .setColor("GREEN")

                for (var i = 0; i < 25; i++) {
                    object = upkeepArray[i];
                    console.log(object)
                    replyEmbed.addFields(
                        {name: 'Faction', value: object.faction, inline: true},
                        {name: "Armies", value: object.numberOfArmies.toString(), inline: true},
                        {name: "Upkeep", value: object.upkeep.toString(), inline: true}
                    )
                }


                replyEmbed.setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to get upkeep data")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })

    }
}
