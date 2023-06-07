package net.critika.ports.club

import net.critika.application.club.command.ClubCommand
import net.critika.application.club.response.ClubResponse
import net.critika.ports.CrudPort

interface ClubCrudPort : CrudPort<ClubCommand, ClubResponse>
