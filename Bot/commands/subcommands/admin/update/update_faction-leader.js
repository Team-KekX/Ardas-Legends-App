const {isMemberStaff, capitalizeFirstLetters} = require("../../../../utils/utilities");
const {MessageEmbed} = require("discord.js");
const {ADMIN} = require("../../../../configs/embed_thumbnails.json");
const {serverIP, serverPort} = require("../../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {

        if (!isMemberStaff(interaction)) {
            await interaction.editReply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }

        const faction = capitalizeFirstLetters(interaction.options.getString("faction-name"))
        const user = interaction.options.getUser("leader")

        const data = {
            factionName: faction,
            targetDiscordId: user.id
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/faction/update/faction-leader', data)
            .then(async function (response) {
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Updated Faction Leader`)
                    .setColor('GREEN')
                    .setDescription(`The player ${user} is now the faction leader of ${faction}.`)
                    .setThumbnail(ADMIN)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function (error) {
                const replyEmbed = new MessageEmbed()
                .setTitle("Error while updating the faction leader")
                .setColor("RED")
                .setDescription(error.response.data.message)
                .setTimestamp()

                await interaction.editReply({embeds: [replyEmbed]})
            })

    },
};
