const {isMemberStaff} = require("../../../../utils/utilities");
const {MessageEmbed} = require("discord.js");
const {ADMIN} = require("../../../../configs/embed_thumbnails.json");
const {serverIP, serverPort} = require("../../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }

        claimbuild = interaction.options.getString("claimbuild");
        faction = interaction.options.getString("faction");

        const data = {
            claimbuildName: claimbuild,
            newFaction: faction
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/claimbuild/update/claimbuild-faction', data)
            .then(async function (response) {
                // The request and data is successful.
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Update Claimbuild Owner`)
                    .setColor('GREEN')
                    .setDescription(`Updated owner of claimbuild "${response.data.claimbuildName}" to be faction "${response.data.newFaction}"`)
                    .setThumbnail(ADMIN)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function (error) {
                const replyEmbed = new MessageEmbed()
                .setTitle("Error while updating owning faction")
                .setColor("RED")
                .setDescription(error.response.data.message)
                .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })

    },
};
