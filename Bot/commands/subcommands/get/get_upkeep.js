const {MessageEmbed} = require('discord.js');
const {UNBIND} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");
const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        const factionName = capitalizeFirstLetters(interaction.options.getString("faction-name"))

        axios.get(`http://${serverIP}:${serverPort}/api/army/upkeep/${factionName}`)
            .then(async function (response) {
                var upkeepDto = response.data;
                var replyEmbed = new MessageEmbed()
                    .setTitle(`Upkeep for faction: ${factionName}`)
                    .setColor("GREEN")
                    .addFields(
                        {name: "Faction", value:upkeepDto.faction, inline: true  },
                        {name: "Number of Armies", value:upkeepDto.numberOfArmies.toString(), inline: true  },
                        {name: "Upkeep", value:upkeepDto.upkeep.toString(), inline: true  },
                    )
                    .setTimestamp()
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
